#!/usr/bin/env bash
set -euo pipefail

# ── 설정 ──────────────────────────────
readonly MODEL="sonnet"
readonly PLAN_FILE=".ai-test/last-plan.md"
readonly MAIN_SRC="src/main/java"
readonly TEST_SRC="src/test/java"

readonly ALLOWED_TOOLS_PLAN=(Read Glob Grep Write)
readonly ALLOWED_TOOLS_TEST=(Read Glob Grep Write Edit
  "Bash(./gradlew:*)" "Bash(find:*)" "Bash(ls:*)")
readonly BUDGET_PLAN="0.50"
readonly BUDGET_TEST="3.00"

# ── 색상 코드 ──────────────────────────
readonly GREEN='\033[0;32m'
readonly YELLOW='\033[1;33m'
readonly RED='\033[0;31m'
readonly CYAN='\033[0;36m'
readonly BOLD='\033[1m'
readonly RESET='\033[0m'

# ── 유틸 함수 ──────────────────────────
info()    { echo -e "${CYAN}[INFO]${RESET} $*"; }
warn()    { echo -e "${YELLOW}[WARN]${RESET} $*"; }
error()   { echo -e "${RED}[ERROR]${RESET} $*" >&2; }
success() { echo -e "${GREEN}[OK]${RESET} $*"; }

# ── 프로젝트 루트로 이동 ──────────────────
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${SCRIPT_DIR}/.."

# ── 초기 검증 ──────────────────────────
if ! command -v claude &>/dev/null; then
    error "Claude Code CLI가 설치되어 있지 않습니다."
    echo "설치: npm install -g @anthropic-ai/claude-code"
    exit 1
fi

mkdir -p .ai-test

# ── 1단계: 테스트 대상 선택 ──────────────
echo ""
echo -e "${BOLD}🧪 AI 테스트 오케스트레이터${RESET}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "테스트 대상을 선택하세요:"
echo "  1) 파일/도메인 선택"
echo "  2) 자연어로 대상 설명"
echo "  3) git diff 기반 (변경된 파일)"
echo ""
echo -n "선택 (1/2/3): "
read -r choice

# ── 파일 선택 헬퍼 ──────────────────────
# 파일 목록에서 번호로 선택하는 함수
select_from_list() {
    local -a files=()
    while IFS= read -r line; do
        [[ -n "$line" ]] && files+=("$line")
    done <<< "$1"

    if [[ ${#files[@]} -eq 0 ]]; then
        return 1
    fi

    if [[ ${#files[@]} -eq 1 ]]; then
        echo "${files[0]}"
        return 0
    fi

    echo "" >&2
    local i=1
    for f in "${files[@]}"; do
        echo -e "  ${CYAN}${i})${RESET} $f" >&2
        ((i++))
    done
    echo "" >&2

    echo -n "번호 선택 (1-${#files[@]}, 또는 a=전체): " >&2
    read -r num

    if [[ "$num" == "a" || "$num" == "A" ]]; then
        printf '%s\n' "${files[@]}"
        return 0
    fi

    if [[ "$num" =~ ^[0-9]+$ ]] && (( num >= 1 && num <= ${#files[@]} )); then
        echo "${files[$((num-1))]}"
        return 0
    fi

    echo "" >&2
    error "잘못된 선택입니다."
    return 1
}

TARGET=""
case "$choice" in
    1)
        echo ""
        echo "입력 방법:"
        echo "  - 도메인명 (예: trip, expense, auth)"
        echo "  - 파일 경로 (예: src/main/java/.../TripService.java)"
        if command -v fzf &>/dev/null; then
            echo "  - 빈 엔터 → fzf 파일 탐색기"
        fi
        echo ""
        echo -n "입력: "
        read -r input

        # 빈 입력 + fzf 설치 시 → fzf 탐색기
        if [[ -z "$input" ]]; then
            if command -v fzf &>/dev/null; then
                info "fzf 파일 탐색기를 엽니다..."
                filepath=$(find "$MAIN_SRC" -name '*.java' -type f | fzf --height=20 --prompt="테스트 대상 선택: ")
                if [[ -z "$filepath" ]]; then
                    info "선택이 취소되었습니다."
                    exit 0
                fi
                TARGET="파일: $filepath"
            else
                error "입력이 비어 있습니다. 도메인명 또는 파일 경로를 입력하세요."
                exit 1
            fi

        # 파일 경로인 경우 (/ 또는 .java 포함)
        elif [[ -f "$input" ]]; then
            TARGET="파일: $input"

        # 도메인명으로 검색
        else
            info "'${input}'으로 파일을 검색합니다..."
            found=$(find "$MAIN_SRC" -path "*/${input}/*" -name '*.java' -type f 2>/dev/null | sort) || true

            if [[ -z "$found" ]]; then
                # 파일명 부분 매칭 시도
                found=$(find "$MAIN_SRC" -name "*${input}*" -name '*.java' -type f 2>/dev/null | sort) || true
            fi

            if [[ -z "$found" ]]; then
                error "'${input}'에 해당하는 파일을 찾을 수 없습니다."
                exit 1
            fi

            selected=$(select_from_list "$found") || exit 1

            # 여러 줄이면 복수 파일
            file_count=$(echo "$selected" | wc -l | tr -d ' ')
            if [[ "$file_count" -eq 1 ]]; then
                info "선택: $selected"
                TARGET="파일: $selected"
            else
                echo ""
                info "선택된 파일 ${file_count}개:"
                echo "$selected" | while read -r f; do echo "  - $f"; done
                TARGET="변경된 파일들:\n$selected"
            fi
        fi
        ;;
    2)
        echo -n "테스트 대상 설명: "
        read -r description
        TARGET="설명: $description"
        ;;
    3)
        info "git diff에서 변경된 Java 파일을 수집합니다..."
        # staged + unstaged 변경
        diff_files=$(git diff --name-only HEAD --diff-filter=ACMR 2>/dev/null || true)
        staged_files=$(git diff --name-only --cached --diff-filter=ACMR 2>/dev/null || true)
        all_files=$(echo -e "${diff_files}\n${staged_files}" | sort -u)

        # 비어있으면 HEAD~1 폴백
        if [[ -z "$all_files" || "$all_files" == $'\n' ]]; then
            warn "현재 변경사항이 없습니다. HEAD~1 대비 diff를 사용합니다."
            all_files=$(git diff --name-only HEAD~1 --diff-filter=ACMR 2>/dev/null || true)
        fi

        # .java만, src/test 제외
        java_files=$(echo "$all_files" | grep '\.java$' | grep -v 'src/test' || true)

        if [[ -z "$java_files" ]]; then
            error "변경된 Java 소스 파일이 없습니다."
            exit 1
        fi

        echo ""
        info "변경된 파일:"
        echo "$java_files" | while read -r f; do echo "  - $f"; done
        echo ""
        TARGET="변경된 파일들:\n$java_files"
        ;;
    *)
        error "잘못된 선택입니다."
        exit 1
        ;;
esac

info "대상: $TARGET"
echo ""

# ── 2단계: 테스트 계획 생성 ──────────────
info "테스트 계획을 생성합니다..."
echo ""

PLAN_PROMPT="프로젝트 루트의 CLAUDE.md를 읽고 테스트 작성 규칙을 숙지하라.

대상: ${TARGET}

대상 소스 코드를 분석하고, .ai-test/last-plan.md에 테스트 계획서를 작성하라.

계획서에 포함할 내용:
1. 대상 클래스/메서드 분석 요약
2. 테스트 슬라이스 선택 이유 (단위 테스트 vs 통합 테스트)
3. 테스트 케이스 목록 (Given-When-Then 형식)
4. 목킹 대상 목록
5. 예상 테스트 파일 경로

⚠️ 테스트 코드는 절대 작성하지 말 것. 계획서만 작성하라.
⚠️ 작업을 완료한 뒤 추가 작업 없이 즉시 종료하라."

claude -p "$PLAN_PROMPT" \
    --output-format text \
    --model "$MODEL" \
    --max-budget-usd "$BUDGET_PLAN" \
    --allowed-tools "${ALLOWED_TOOLS_PLAN[@]}"

if [[ ! -f "$PLAN_FILE" ]]; then
    error "계획서 생성에 실패했습니다."
    exit 1
fi

success "계획서가 생성되었습니다: $PLAN_FILE"
echo ""

# ── 3단계: 사용자 검증 루프 ──────────────
while true; do
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo -e "${BOLD}📋 테스트 계획서${RESET}"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    cat "$PLAN_FILE"
    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""
    echo "  y) 계획 승인 → 테스트 생성 진행"
    echo "  e) 피드백 입력 → 계획 재생성"
    echo "  v) 에디터로 계획 직접 편집"
    echo "  n) 종료"
    echo ""
    echo -n "선택 (y/e/v/n): "
    read -r action

    case "$action" in
        y|Y)
            success "계획이 승인되었습니다. 테스트를 생성합니다."
            break
            ;;
        e|E)
            echo -n "피드백: "
            read -r feedback
            info "피드백을 반영하여 계획을 재생성합니다..."
            echo ""

            FEEDBACK_PROMPT="프로젝트 루트의 CLAUDE.md를 읽고 테스트 작성 규칙을 숙지하라.

기존 테스트 계획서(.ai-test/last-plan.md)를 읽고, 다음 피드백을 반영하여 수정하라:

피드백: ${feedback}

수정된 계획서를 .ai-test/last-plan.md에 덮어쓰기하라.
⚠️ 테스트 코드는 절대 작성하지 말 것. 계획서만 수정하라.
⚠️ 작업을 완료한 뒤 추가 작업 없이 즉시 종료하라."

            claude -p "$FEEDBACK_PROMPT" \
                --output-format text \
                --model "$MODEL" \
                --max-budget-usd "$BUDGET_PLAN" \
                --allowed-tools "${ALLOWED_TOOLS_PLAN[@]}"

            success "계획서가 재생성되었습니다."
            echo ""
            ;;
        v|V)
            ${EDITOR:-vi} "$PLAN_FILE"
            success "계획서가 편집되었습니다."
            echo ""
            ;;
        n|N)
            info "종료합니다."
            exit 0
            ;;
        *)
            warn "잘못된 입력입니다. y/e/v/n 중 선택하세요."
            ;;
    esac
done

echo ""

# ── 4단계: 테스트 생성 및 실행 ──────────────
info "테스트를 생성하고 실행합니다..."
echo ""

GEN_PROMPT="프로젝트 루트의 CLAUDE.md를 읽고 테스트 작성 규칙을 엄격히 준수하라.

.ai-test/last-plan.md의 계획서를 읽고, 계획에 따라 테스트를 작성하라.

규칙:
- 각 테스트 파일 작성 후 ./gradlew test --tests '테스트클래스명'으로 실행하여 통과 확인
- 테스트가 실패하면 원인을 분석하고 수정하라 — 최대 5회 반복
- 5회 반복 후에도 실패하면 실패 원인을 보고하고 종료
- 프로덕션 코드(src/main) 절대 수정 금지
- assertion이 충분히 구체적인지 스스로 검증
- @ActiveProfiles(\"test\") 반드시 추가
⚠️ 작업을 완료한 뒤 추가 작업 없이 즉시 종료하라."

claude -p "$GEN_PROMPT" \
    --output-format text \
    --model "$MODEL" \
    --max-budget-usd "$BUDGET_TEST" \
    --allowed-tools "${ALLOWED_TOOLS_TEST[@]}"

echo ""

# ── 결과 출력 ──────────────────────────
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo -e "${BOLD}📊 생성된 테스트 파일${RESET}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
git status --short "$TEST_SRC/" 2>/dev/null || echo "(변경사항 없음)"
echo ""
success "AI 테스트 오케스트레이터 완료!"
