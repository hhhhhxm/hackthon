package com.kezaihui.faq.service;

import com.kezaihui.faq.dataObject.DialogueStatus;
import com.kezaihui.faq.vo.AnswerResultVo;

import java.io.IOException;

/**
 * @Author: lerry_li
 * @CreateDate: 2022/03/31
 * @Description
 */
public interface DialogueService {
    /**
     * 回答用户问题
     *
     * @param question 初始的对话状态模型
     * @return 完成的对话状态模型
     */
    AnswerResultVo answer2(String question) throws IOException;

    /**
     * 回答用户问题json
     *
     * @param dialogueStatus 初始的对话状态模型
     * @return 完成的对话状态模型
     */
    DialogueStatus answer(DialogueStatus dialogueStatus) throws IOException;
}
