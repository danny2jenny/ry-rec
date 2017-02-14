package com.rytec.rec.db.mapper;

import com.rytec.rec.db.model.ActionRule;
import com.rytec.rec.db.model.ActionRuleExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface ActionRuleMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table actionrule
     *
     * @mbggenerated Tue Feb 14 13:07:30 CST 2017
     */
    int countByExample(ActionRuleExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table actionrule
     *
     * @mbggenerated Tue Feb 14 13:07:30 CST 2017
     */
    int deleteByExample(ActionRuleExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table actionrule
     *
     * @mbggenerated Tue Feb 14 13:07:30 CST 2017
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table actionrule
     *
     * @mbggenerated Tue Feb 14 13:07:30 CST 2017
     */
    int insert(ActionRule record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table actionrule
     *
     * @mbggenerated Tue Feb 14 13:07:30 CST 2017
     */
    int insertSelective(ActionRule record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table actionrule
     *
     * @mbggenerated Tue Feb 14 13:07:30 CST 2017
     */
    List<ActionRule> selectByExampleWithRowbounds(ActionRuleExample example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table actionrule
     *
     * @mbggenerated Tue Feb 14 13:07:30 CST 2017
     */
    List<ActionRule> selectByExample(ActionRuleExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table actionrule
     *
     * @mbggenerated Tue Feb 14 13:07:30 CST 2017
     */
    ActionRule selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table actionrule
     *
     * @mbggenerated Tue Feb 14 13:07:30 CST 2017
     */
    int updateByExampleSelective(@Param("record") ActionRule record, @Param("example") ActionRuleExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table actionrule
     *
     * @mbggenerated Tue Feb 14 13:07:30 CST 2017
     */
    int updateByExample(@Param("record") ActionRule record, @Param("example") ActionRuleExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table actionrule
     *
     * @mbggenerated Tue Feb 14 13:07:30 CST 2017
     */
    int updateByPrimaryKeySelective(ActionRule record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table actionrule
     *
     * @mbggenerated Tue Feb 14 13:07:30 CST 2017
     */
    int updateByPrimaryKey(ActionRule record);
}