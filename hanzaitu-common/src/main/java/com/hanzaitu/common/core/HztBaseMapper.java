package com.hanzaitu.common.core;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.hanzaitu.common.core.mybatis.HztLambdaQueryChainWrapper;
import com.hanzaitu.common.core.mybatis.MybatisPage;
import com.hanzaitu.common.core.result.ResultException;
import com.hanzaitu.common.page.HztPage;
import com.hanzaitu.common.page.Pageable;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.crypto.Data;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

/**
 * 1. NULL字段不参与插入和更新操作
 *
 * @param <T>
 */
@org.apache.ibatis.annotations.Mapper
public interface HztBaseMapper<T> extends BaseMapper<T> {

    /**
     * 默认批次提交数量
     */
    int DEFAULT_BATCH_SIZE = 1000;

    /**
     * 扩展字段，Map结构
     */
    String EXTEND_FIELDS = "extendFields";

    default Class<?> currentMapperClass() {
        return this.getClass().getInterfaces()[0];
    }

    default Class<T> currentModelClass() {
        return (Class<T>) ((ParameterizedType)this.currentMapperClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
    }

    default String getSqlStatement(SqlMethod sqlMethod) {
        return SqlHelper.getSqlStatement(currentMapperClass(), sqlMethod);
    }

    /**
     * 链式查询 普通
     *
     * @return QueryWrapper 的包装类
     */
    default QueryChainWrapper<T> query() {
        return ChainWrappers.queryChain(this);
    }

    /**
     * 链式查询 lambda 式
     * <p>注意：不支持 Kotlin </p>
     *
     * @return LambdaQueryWrapper 的包装类
     */
    default LambdaQueryChainWrapper<T> lambdaQuery() {
        return ChainWrappers.lambdaQueryChain(this);
    }



    /**
     * 链式更改 lambda 式
     * <p>注意：不支持 Kotlin </p>
     *
     * @return LambdaUpdateWrapper 的包装类
     */
    default LambdaUpdateChainWrapper<T> lambdaUpdate() {
        return ChainWrappers.lambdaUpdateChain(this);
    }

    /**
     * 链式更改 lambda 式
     * <p>注意：不支持 Kotlin </p>
     *
     * @return LambdaUpdateWrapper 的包装类
     */
    default HztLambdaQueryChainWrapper<T> hztLambdaQuery(){
        return new HztLambdaQueryChainWrapper<>(lambdaQuery());
    }

    /**
     * 批量保存或者更新，被调用方法必须加事务注解
     * 每1000条提交一次
     * 强制设置更新时间为null，便于拦截器更新为系统当前时间
     * @param entityList
     * @param <T>
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    default <T> boolean saveOrUpdateBatch(Collection<T> entityList) {
        Log log = LogFactory.getLog(currentMapperClass().getClass());
        TableInfo tableInfo = TableInfoHelper.getTableInfo(currentModelClass());
        Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
        String keyProperty = tableInfo.getKeyProperty();
        Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");
        return SqlHelper.saveOrUpdateBatch(currentModelClass(), currentMapperClass(), log, entityList, DEFAULT_BATCH_SIZE, (sqlSession, entity) -> {
            Object idVal = tableInfo.getPropertyValue(entity, keyProperty);
            return StringUtils.checkValNull(idVal)
                    || CollectionUtils.isEmpty(sqlSession.selectList(getSqlStatement(SqlMethod.SELECT_BY_ID), entity));
        }, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
            param.put(Constants.ENTITY, entity);
            sqlSession.update(getSqlStatement(SqlMethod.UPDATE_BY_ID), param);
        });
    }

    /**
     * 批量插入，被调用方法必须加事务注解
     * 每1000条提交一次
     * @param entityList
     * @param <T>
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    default <T> boolean saveBatch(Collection<T> entityList) {
        Log log = LogFactory.getLog(currentMapperClass().getClass());
        String sqlStatement = this.getSqlStatement(SqlMethod.INSERT_ONE);
        return SqlHelper.executeBatch(currentModelClass(),
                log, entityList, DEFAULT_BATCH_SIZE, (sqlSession, entity) -> {
            sqlSession.insert(sqlStatement, entity);
        });
    }

    /**
     * 新增或者根据Id更新
     * 强制设置更新时间为null，便于拦截器更新为系统当前时间
     * @param entity
     * @return
     */
    default int saveOrUpdateById(T entity){
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entity.getClass());
        Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
        String keyProperty = tableInfo.getKeyProperty();
        Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");
        Object idVal = tableInfo.getPropertyValue(entity, tableInfo.getKeyProperty());
        if (Objects.isNull(idVal)) {
            return this.insert(entity);
        }else{
            return this.updateById(entity);
        }
    }

    /**
     * 检查数据库是否更新成功
     * @param mapperFunction
     * @param param
     */
    default <E> void check(Function<E, Integer> mapperFunction, E param){
        Assert.isTrue(mapperFunction.apply(param)>0,"数据库更新失败");
    }

    /**
     * 判断某个字段的值是否在表中存在
     * @param fun
     * @param value
     * @return
     */
    default boolean isExist(SFunction<T, ?> fun, Object value){
        return this.exists(this.lambdaQuery().eq(fun, value).getWrapper());
    }

    default boolean isExist(SFunction<T, ?> fun1, Object value1, SFunction<T, ?> fun2, Object value2){
        return this.exists(this.lambdaQuery().eq(fun1, value1).eq(fun2, value2).getWrapper());
    }

    /**
     * 插入时：过滤掉逻辑删除字段, 乐观锁字段，逻辑删除和乐观锁字段数据库给默认值
     * 与saveBatch不同，插入的语句是1条，样式为Insert into user(...) values(...)(...)(...)
     * 如果数据量不大，推荐使用这个方式
     * 不受全局策略影响，如果字段为null也会插入
     * @param entityList
     * @return
     */
    int insertBatchSomeColumn(@Param("list") List<T> entityList);

    /**
     * 查询所有
     * @return
     */
    default List<T> selectAll(){
        return this.selectList(null);
    }

    /**
     * 接受一个参数查询，等于匹配
     * @param fun1
     * @param value1
     * @return
     */
    default List<T> selectList(SFunction<T, ?> fun1, Object value1){
        return  this.selectList(this.lambdaQuery().eq(fun1, value1).getWrapper());
    }


    /**
     * 接受两个参数查询，等于匹配
     * @param fun1
     * @param value1
     * @param fun2
     * @param value2
     * @return
     */
    default List<T> selectList(SFunction<T, ?> fun1, Object value1, SFunction<T, ?> fun2, Object value2){
        return  this.selectList(this.lambdaQuery().eq(fun1, value1).eq(fun2, value2).getWrapper());
    }



    /**
     * 接受三个查询条件, 等于匹配
     * @param fun1
     * @param value1
     * @param fun2
     * @param value2
     * @param fun3
     * @param value3
     * @return
     */
    default List<T> selectList(SFunction<T, ?> fun1, Object value1, SFunction<T, ?> fun2, Object value2, SFunction<T, ?> fun3, Object value3){
        return  this.selectList(this.lambdaQuery().eq(fun1, value1).eq(fun2, value2).eq(fun3, value3).getWrapper());
    }

    /**
     * 接受一个参数查询，等于匹配
     * @param fun1
     * @param value1
     * @return 未找到返回null，大于1抛出异常
     */
    default T selectOne(SFunction<T, ?> fun1, Object value1){
        List<T> list =  this.selectList(this.lambdaQuery().eq(fun1, value1).getWrapper());
        if(list.size() > 1){
            throw ResultException.createResultException("数据错误，查询的结果大于1");
        }else if(list.size() == 0){
            return null;
        }
        return list.get(0);
    }


    /**
     * 接受两个参数查询，等于匹配
     * @param fun1
     * @param value1
     * @param fun2
     * @param value2
     * @return 未找到返回null，大于1抛出异常
     */
    default T selectOne(SFunction<T, ?> fun1, Object value1, SFunction<T, ?> fun2, Object value2){
        List<T> list =  this.selectList(this.lambdaQuery().eq(fun1, value1).eq(fun2, value2).getWrapper());
        if(list.size() > 1){
            throw ResultException.createResultException("数据错误，查询的结果大于1");
        }else if(list.size() == 0){
            return null;
        }
        return list.get(0);
    }



    /**
     * 接受三个查询条件, 等于匹配
     * @param fun1
     * @param value1
     * @param fun2
     * @param value2
     * @param fun3
     * @param value3
     * @return 未找到返回null，大于1抛出异常
     */
    default T selectOne(SFunction<T, ?> fun1, Object value1, SFunction<T, ?> fun2, Object value2, SFunction<T, ?> fun3, Object value3){
        List<T> list =  this.selectList(this.lambdaQuery().eq(fun1, value1).eq(fun2, value2).eq(fun3, value3).getWrapper());
        if(list.size() > 1){
            throw ResultException.createResultException("数据错误，查询的结果大于1");
        }else if(list.size() == 0){
            return null;
        }
        return list.get(0);
    }

    /**
     * 接受一个参数查询，等于匹配
     * @param fun1
     * @param value1
     * @return
     */
    default Long selectCount(SFunction<T, ?> fun1, Object value1){
        return this.selectCount(this.lambdaQuery().eq(fun1, value1).getWrapper());
    }

    /**
     * 接受两个参数查询，等于匹配
     * @param fun1
     * @param value1
     * @param fun2
     * @param value2
     * @return
     */
    default Long selectCount(SFunction<T, ?> fun1, Object value1, SFunction<T, ?> fun2, Object value2){
        return  this.selectCount(this.lambdaQuery().eq(fun1, value1).eq(fun2, value2).getWrapper());
    }

    /**
     * 接受三个查询条件, 等于匹配
     * @param fun1
     * @param value1
     * @param fun2
     * @param value2
     * @param fun3
     * @param value3
     * @return
     */
    default Long selectCount(SFunction<T, ?> fun1, Object value1, SFunction<T, ?> fun2, Object value2, SFunction<T, ?> fun3, Object value3){
        return  this.selectCount(this.lambdaQuery().eq(fun1, value1).eq(fun2, value2).eq(fun3, value3).getWrapper());
    }

    /**
     * 返回Map对象，key为指定的key，value为结果对象
     * @param fun
     * @param queryWrapper
     * @return
     */
    default Map<Serializable, T> selectMap(Function<T, Serializable> fun, Wrapper<T> queryWrapper){
        Map<Serializable, T> map = new HashMap<>();
        List<T> list = this.selectList(queryWrapper);
        list.forEach(item->{
            Serializable key = fun.apply(item);
            map.put(key, item);
        });
        return map;
    }



    /**
     * 链式更改 普通
     *
     * @return UpdateWrapper 的包装类
     */
    default UpdateChainWrapper<T> update() {
        return ChainWrappers.updateChain(this);
    }

    /**
     * 更新时：过滤掉填充策略是 INSERT 的字段，逻辑删除字段
     * 不受全局策略影响，如果字段为null也会更新
     * 其他update方法，受全局策略影响
     * @param entity 字段为null的字典，数据库字段会被设置为null！！！
     * @return
     */
    int alwaysUpdateSomeColumnById(@Param(Constants.ENTITY) T entity);

    /**
     * 根据In条件查询
     * @param fun1
     * @param keys
     * @return
     */
    default List<T> selectListByIn(SFunction<T, ?> fun1, Collection<?> keys){
        return  this.selectList(this.lambdaQuery().in(fun1, keys).getWrapper());
    }

    default boolean updateById(SFunction<T, Serializable> idColumn, Serializable idVal, SFunction<T, Serializable> column, Object val){
        return this.lambdaUpdate().eq(idColumn, idVal).set(column, val).update();
    }

    default boolean updateById(SFunction<T, Serializable> idColumn, Serializable idVal, SFunction<T, ?> fun1, Object value1, SFunction<T, ?> fun2, Object value2){
        return this.lambdaUpdate().eq(idColumn, idVal).set(fun1, value1).set(fun2, value2).update();
    }

    default boolean updateById(SFunction<T, Serializable> idColumn, Serializable idVal, SFunction<T, ?> fun1, Object value1, SFunction<T, ?> fun2, Object value2, SFunction<T, ?> fun3, Object value3){
        return this.lambdaUpdate().eq(idColumn, idVal).set(fun1, value1).set(fun2, value2).set(fun3, value3).update();
    }

    /**
     * 批量更新，被调用方法必须加事务注解
     * 每1000条提交一次
     * 强制设置更新时间为null，便于拦截器更新为系统当前时间
     * @param entityList
     * @param <T>
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    default <T> boolean updateBatchById(Collection<T> entityList) {
        Log log = LogFactory.getLog(currentMapperClass().getClass());
        String sqlStatement = getSqlStatement(SqlMethod.UPDATE_BY_ID);
        return SqlHelper.executeBatch(currentModelClass(), log, entityList, DEFAULT_BATCH_SIZE, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
            param.put(Constants.ENTITY, entity);
            sqlSession.update(sqlStatement, param);
        });
    }



    /**
     * 接受一个参数查询，等于匹配
     * @param fun1
     * @param value1
     * @return
     */
    default int delete(SFunction<T, ?> fun1, Object value1){
        return  this.delete(this.lambdaQuery().eq(fun1, value1).getWrapper());
    }

    /**
     * 接受两个参数查询，等于匹配
     * @param fun1
     * @param value1
     * @param fun2
     * @param value2
     * @return
     */
    default int delete(SFunction<T, ?> fun1, Object value1, SFunction<T, ?> fun2, Object value2){
        return  this.delete(this.lambdaQuery().eq(fun1, value1).eq(fun2, value2).getWrapper());
    }

    /**
     * 接受三个查询条件, 等于匹配
     * @param fun1
     * @param value1
     * @param fun2
     * @param value2
     * @param fun3
     * @param value3
     * @return
     */
    default int delete(SFunction<T, ?> fun1, Object value1, SFunction<T, ?> fun2, Object value2, SFunction<T, ?> fun3, Object value3){
        return  this.delete(this.lambdaQuery().eq(fun1, value1).eq(fun2, value2).eq(fun3, value3).getWrapper());
    }

    /**
     * 批量删除，被调用方法必须加事务注解
     * 每1000条提交一次
     * @param entityList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    default boolean deleteBatchByIds(Collection<T> entityList) {
        Log log = LogFactory.getLog(currentMapperClass().getClass());
        String sqlStatement = getSqlStatement(SqlMethod.DELETE_BY_ID);
        Class entityClass = currentModelClass();
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
        boolean useFill = tableInfo.isWithLogicDelete() && tableInfo.isWithUpdateFill();
        return SqlHelper.executeBatch(entityClass, log, entityList, DEFAULT_BATCH_SIZE, (sqlSession, entity) -> {
            if (useFill && tableInfo.isWithLogicDelete()) {
                if (entityClass.isAssignableFrom(entity.getClass())) {
                    sqlSession.update(sqlStatement, entity);
                } else {
                    T instance = tableInfo.newInstance();
                    tableInfo.setPropertyValue(instance, tableInfo.getKeyProperty(), entity);
                    sqlSession.update(sqlStatement, instance);
                }
            } else {
                sqlSession.update(sqlStatement, entity);
            }
        });
    }

    default int getSqlType(Object value){
        int type = Types.VARCHAR;
        if(value instanceof BigDecimal){
            type = Types.DECIMAL;
        }else if(value instanceof Data || value instanceof LocalDateTime || value instanceof LocalDate){
            type = Types.TIMESTAMP;
        }else if(value instanceof Long){
            type = Types.BIGINT;
        }else if(value instanceof Integer){
            type = Types.INTEGER;
        }
        return type;
    }

    /**
     * 私有方法
     * @param ids
     * @return
     */
    default List<Map<String, Object>> _selectByIdForExtendFields(Collection<Serializable> ids){
        TableInfo tableInfo = TableInfoHelper.getTableInfo(currentModelClass());
        List<Map<String, Object>> list = this.selectMaps(this.query().select("*").in(tableInfo.getKeyProperty(), ids).getWrapper());
        return list;
    }

    /**
     * 私有方法
     * @param wrapper
     * @return
     */
    default List<Map<String, Object>> _selectByWrapperForExtendFields(QueryChainWrapper<T> wrapper){
        List<Map<String, Object>> list = this.selectMaps(wrapper.select("*").getWrapper());
        return list;
    }


    /**
     * 单表分页查询
     * @param page
     * @param queryWrapper
     * @return
     */
    default HztPage<T> selectPage(Pageable page, AbstractWrapper<T, ?, ?> queryWrapper){
        if(page.isAllowPaging()){
            return this.selectPage(MybatisPage.convert(page), queryWrapper).convert();
        }else{
            return HztPage.convert(this.selectList(queryWrapper));
        }
    }
}

