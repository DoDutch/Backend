package graduation.project.DoDutch_server.domain.photo.repository;

import graduation.project.DoDutch_server.domain.photo.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo,Long> {
}
