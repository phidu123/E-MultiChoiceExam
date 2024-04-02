package vn.edu.tdtu.exam.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.exam.entity.Option;
import vn.edu.tdtu.exam.entity.Question;
import vn.edu.tdtu.exam.repository.OptionRepository;

import java.util.List;

@Service
public class OptionService {

    @Autowired
    private OptionRepository optionRepository;

    public Option addOption(Option option) {
        return optionRepository.save(option);
    }

    public List<Option> getOptionByQuestion(Question question){
        return optionRepository.findAllOptionByQuestion(question);
    }
    public Option getTrueOptionByQuestion(Question question){
        return optionRepository.findOptionByisCorrectTrueAndQuestion(question);
    }
    public Boolean checkTrueOptionByQuestion(Question question, Long optionId){
        Option option =  optionRepository.findOptionByIdAndQuestion(optionId,question);
        if(option.getIsCorrect())
            return true;
        return false;
    }
}
