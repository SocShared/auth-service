package ml.socshared.template.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ml.socshared.template.domain.TestObject;

@Repository
public interface TestRepository extends JpaRepository<TestObject, Integer> {
}
