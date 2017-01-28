package com.rytec.rec.db.mapper;

import com.rytec.rec.db.model.DeviceGis;
import com.rytec.rec.db.model.DeviceGisExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface DeviceGisMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table devicegis
     *
     * @mbggenerated Tue Jan 03 11:26:54 CST 2017
     */
    int countByExample(DeviceGisExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table devicegis
     *
     * @mbggenerated Tue Jan 03 11:26:54 CST 2017
     */
    int deleteByExample(DeviceGisExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table devicegis
     *
     * @mbggenerated Tue Jan 03 11:26:54 CST 2017
     */
    int insert(DeviceGis record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table devicegis
     *
     * @mbggenerated Tue Jan 03 11:26:54 CST 2017
     */
    int insertSelective(DeviceGis record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table devicegis
     *
     * @mbggenerated Tue Jan 03 11:26:54 CST 2017
     */
    List<DeviceGis> selectByExampleWithRowbounds(DeviceGisExample example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table devicegis
     *
     * @mbggenerated Tue Jan 03 11:26:54 CST 2017
     */
    List<DeviceGis> selectByExample(DeviceGisExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table devicegis
     *
     * @mbggenerated Tue Jan 03 11:26:54 CST 2017
     */
    int updateByExampleSelective(@Param("record") DeviceGis record, @Param("example") DeviceGisExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table devicegis
     *
     * @mbggenerated Tue Jan 03 11:26:54 CST 2017
     */
    int updateByExample(@Param("record") DeviceGis record, @Param("example") DeviceGisExample example);
}