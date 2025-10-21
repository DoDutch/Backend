package graduation.project.DoDutch_server.domain.trip.dto.Request;

import java.util.*;

public class FeatureDto {

    // 순서를 고정 시키기 위해 LinkedHashMap을 사용
    private final Map<String, Float> features = new LinkedHashMap<>();

    public FeatureDto() {
        // 초기 key 등록
        features.put("MONTH", 0f);
        features.put("NUM_COMPANIONS", 0f);
        features.put("IS_HOLIDAY", 0f);

        features.put("LOCATION_강원", 0f);
        features.put("LOCATION_경기", 0f);
        features.put("LOCATION_경남", 0f);
        features.put("LOCATION_경북", 0f);
        features.put("LOCATION_광주", 0f);
        features.put("LOCATION_대구", 0f);
        features.put("LOCATION_대전", 0f);
        features.put("LOCATION_도서 지역", 0f);
        features.put("LOCATION_부산", 0f);
        features.put("LOCATION_서울", 0f);
        features.put("LOCATION_세종", 0f);
        features.put("LOCATION_울산", 0f);
        features.put("LOCATION_인천", 0f);
        features.put("LOCATION_전남", 0f);
        features.put("LOCATION_전북", 0f);
        features.put("LOCATION_제주", 0f);
        features.put("LOCATION_충남", 0f);
        features.put("LOCATION_충북", 0f);

        features.put("DURATION_CATEGORY_1박 2일", 0f);
        features.put("DURATION_CATEGORY_2박 3일", 0f);
        features.put("DURATION_CATEGORY_3박 4일 이상", 0f);
        features.put("DURATION_CATEGORY_당일", 0f);
    }

    /** 특정 feature 값 설정 */
    public void setFeature(String key, float value) {
        if (features.containsKey(key)) {
            features.put(key, value);
        }
    }

    /** Flask로 보낼 때 feature 순서대로 value 리스트 변환 */
    public List<Float> toValueList() {
        return new ArrayList<>(features.values());
    }
}
