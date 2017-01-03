package com.rytec.rec.db.mapper;

import com.rytec.rec.db.model.GisLayer;
import com.rytec.rec.db.model.GisLayerExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface GisLayerMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gislayer
     *
     * @mbggenerated Fri Dec 30 09:40:15 CST 2016
     */
    int countByExample(GisLayerExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gislayer
     *
     * @mbggenerated Fri Dec 30 09:40:15 CST 2016
     */
    int deleteByExample(GisLayerExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gislayer
     *
     * @mbggenerated Fri Dec 30 09:40:15 CST 2016
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gislayer
     *
     * @mbggenerated Fri Dec 30 09:40:15 CST 2016
     */
    int insert(GisLayer record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gislayer
     *
     * @mbggenerated Fri Dec 30 09:40:15 CST 2016
     */
    int insertSelective(GisLayer record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gislayer
     *
     * @mbggenerated Fri Dec 30 09:40:15 CST 2016
     */
    List<GisLayer> selectByExampleWithRowbounds(GisLayerExample example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gislayer
     *
     * @mbggenerated Fri Dec 30 09:40:15 CST 2016
     */
    List<GisLayer> selectByExample(GisLayerExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gislayer
     *
     * @mbggenerated Fri Dec 30 09:40:15 CST 2016
     */
    GisLayer selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gislayer
     *
     * @mbggenerated Fri Dec 30 09:40:15 CST 2016
     */
    int updateByExampleSelective(@Param("record") GisLayer record, @Param("example") GisLayerExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gislayer
     *
     * @mbggenerated Fri Dec 30 09:40:15 CST 2016
     */
    int updateByExample(@Param("record") GisLayer record, @Param("example") GisLayerExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gislayer
     *
     * @mbggenerated Fri Dec 30 09:40:15 CST 2016
     */
    int updateByPrimaryKeySelective(GisLayer record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gislayer
     *
     * @mbggenerated Fri Dec 30 09:40:15 CST 2016
     */
    int updateByPrimaryKey(GisLayer record);
}