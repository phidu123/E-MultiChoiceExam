package vn.edu.tdtu.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.exam.entity.Option;
import vn.edu.tdtu.exam.entity.Question;

import java.util.List;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {
    List<Option> findAllOptionByQuestion(Question question);
    Option findOptionByisCorrectTrueAndQuestion(Question question);
    Option findOptionByIdAndQuestion(Long id, Question question);


}
