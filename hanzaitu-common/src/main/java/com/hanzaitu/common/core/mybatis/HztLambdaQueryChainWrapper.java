package com.hanzaitu.common.core.mybatis;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;

import java.io.Serializable;
import java.util.Objects;

/**
 * 继承 LambdaQueryChainWrapper
 *
 **/
public class HztLambdaQueryChainWrapper<T>  {

    private LambdaQueryChainWrapper<T> lambdaQueryChainWrapper;

    public HztLambdaQueryChainWrapper(LambdaQueryChainWrapper lambdaQueryChainWrapper) {
        this.lambdaQueryChainWrapper = lambdaQueryChainWrapper;
    }

    /**
     * 非空等于
     * @param column
     * @param val
     * @return
     */
    public HztLambdaQueryChainWrapper<T> eqNotEmpty(SFunction<T, Serializable> column, String  val) {
        lambdaQueryChainWrapper.eq(StringUtils.isNotEmpty(val), column, val);
        return this;
    }

    /**
     * 非空不等于
     * @param column
     * @param val
     * @return
     */
    public HztLambdaQueryChainWrapper<T> neNotEmpty(SFunction<T, Serializable> column, String  val) {
        lambdaQueryChainWrapper.ne(StringUtils.isNotEmpty(val), column, val);
        return this;
    }

    /**
     * 非null等于
     * @param column
     * @param val
     * @return
     */
    public HztLambdaQueryChainWrapper<T> eqNotNull(SFunction<T, Serializable> column, Object  val) {
        lambdaQueryChainWrapper.eq(Objects.nonNull(val), column, val);
        return this;
    }

    /**
     * 非null不等于
     * @param column
     * @param val
     * @return
     */
    public HztLambdaQueryChainWrapper<T> neNotNull(SFunction<T, Serializable> column, Object  val) {
        lambdaQueryChainWrapper.ne(Objects.nonNull(val), column, val);
        return this;
    }

    /**
     * 非空 like
     * @param column
     * @param val
     * @return
     */
    public HztLambdaQueryChainWrapper<T>  likeNotEmpty(SFunction<T, Serializable> column, String  val) {
        lambdaQueryChainWrapper.like(StringUtils.isNotEmpty(val), column, val);
        return this;
    }

    /**
     * 大于
     * @param column
     * @param val
     * @return
     */
    public HztLambdaQueryChainWrapper<T>  gtNotNull(SFunction<T, Serializable> column, Object  val) {
        lambdaQueryChainWrapper.gt(Objects.nonNull(val), column, val);
        return this;
    }

    /**
     * 大于等于
     * @param column
     * @param val
     * @return
     */
    public HztLambdaQueryChainWrapper<T>  geNotNull(SFunction<T, Serializable> column, Object  val) {
        lambdaQueryChainWrapper.ge(Objects.nonNull(val), column, val);
        return this;
    }

    /**
     * 小于
     * @param column
     * @param val
     * @return
     */
    public HztLambdaQueryChainWrapper<T>  ltNotNull(SFunction<T, Serializable> column, Object  val) {
        lambdaQueryChainWrapper.lt(Objects.nonNull(val), column, val);
        return this;
    }

    /**
     * 小于等于
     * @param column
     * @param val
     * @return
     */
    public HztLambdaQueryChainWrapper<T>  leNotNull(SFunction<T, Serializable> column, Object  val) {
        lambdaQueryChainWrapper.le(Objects.nonNull(val), column, val);
        return this;
    }

    /**
     * 获取原始对象，拆包
     * @return
     */
    public LambdaQueryChainWrapper<T> unwrap(){
        return lambdaQueryChainWrapper;
    }

    public AbstractWrapper getWrapper(){
        return lambdaQueryChainWrapper.getWrapper();
    }

}
