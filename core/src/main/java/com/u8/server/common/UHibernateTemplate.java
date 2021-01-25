package com.u8.server.common;

import org.hibernate.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/***
 * Hibernate Dao 层默认继承该类
 * @param <T>
 * @param <PK>
 */
public class UHibernateTemplate<T, PK extends Serializable>
        implements IHibernateTemplate<T, PK> {
    public SessionFactory sessionFactory;
    protected Class<T> entityClass;

    @SuppressWarnings("unchecked")
    public UHibernateTemplate() {
        this.entityClass = null;
        Class localClass = getClass();
        Type localType = localClass.getGenericSuperclass();
        if ((localType instanceof ParameterizedType)) {
            Type[] arrayOfType = ((ParameterizedType) localType).getActualTypeArguments();
            this.entityClass = ((Class) arrayOfType[0]);
        }
    }

    public UHibernateTemplate(SessionFactory sessionFactory, Class<T> clazz) {
        this.sessionFactory = sessionFactory;
        this.entityClass = clazz;
    }

    public Session getSession() {
        return this.sessionFactory.getCurrentSession();
    }

    @SuppressWarnings("unchecked")
    public T get(PK id) {
        return (T) getSession().get(this.entityClass, id);
    }

    public void save(T data) {
        Assert.notNull(data);
        getSession().saveOrUpdate(data);
    }

    public void insert(T data) {
        Assert.notNull(data);
        getSession().save(data);
    }

    public void mergeSave(T data) {
        Assert.notNull(data);
        getSession().merge(data);
    }

    public void delete(T data) {
        Assert.notNull(data);
        getSession().delete(data);
    }

    public void delete(PK id) {
        delete(get(id));
    }

    @SuppressWarnings("unchecked")
    public List<T> findAll(OrderParameters orderParams) {
        Criteria localCriteria = getCriteria();
        if (orderParams != null) {
            Iterator localIterator = orderParams.getParameters().iterator();
            while (localIterator.hasNext()) {
                OrderParameter localOrderParameter = (OrderParameter) localIterator.next();
                localCriteria.addOrder(localOrderParameter.getOrder());
            }
        }
        return new ArrayList(localCriteria.list());
    }

    public List<T> findAll() {

        return findAll(null);
    }

    public Criteria getCriteria() {
        return getSession().createCriteria(this.entityClass);
    }


    public List find(String sql, Object[] params, OrderParameters orderParams) {
        Assert.hasText(sql);
        sql = sql + (orderParams == null ? "" : orderParams.toString());
        return createQuery(sql, params).list();
    }

    @SuppressWarnings("unchecked")
    public Page<T> find(PageParameter page, String sql, Object[] params, OrderParameters orderParams) {
        Assert.notNull(page);
        Assert.notNull(sql);
        Page localPage = new Page();
        localPage.setPageParameter(page);
        Query localQuery;
        if (page.isAutoCount()) {
            String str = "select count(1) " + sql;
            localQuery = createQuery(str, params);
            List lst = localQuery.list();
            localPage.setTotalCount((Long) lst.get(0));
        }
        if (localPage.getTotalCount() > 0) {
            sql = sql + (orderParams == null ? "" : orderParams.toString());
            localQuery = createQuery(sql, params);
            if (page.isFirstSetted())
                localQuery.setFirstResult(page.getFirst());
            if (page.isPageSizeSetted())
                localQuery.setMaxResults(page.getPageSize());
            localPage.setResultList(localQuery.list());
            return localPage;
        }
        return null;
    }

    //将HQL语句转为jpa格式， ?0...?1...?2...
    private String formatJPAHQL(String sql) {

        Assert.hasText(sql);

        StringBuilder sb = new StringBuilder();
        int markIndex = 0;
        for (int i = 0; i < sql.length(); i++) {
            String c = sql.substring(i, i + 1);
            if ("?".equals(c)) {
                sb.append("?" + markIndex);
                markIndex++;
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    public Query createQuery(String sql, Object[] params) {
        Assert.hasText(sql);

        sql = formatJPAHQL(sql);

        Query localQuery = getSession().createQuery(sql);
        if (params != null)
            for (int i = 0; i < params.length; i++) {
                if (params[i] instanceof Date) {
                    localQuery.setTimestamp(i + "", (Date) params[i]);
                } else {
                    localQuery.setParameter(i + "", params[i]);
                }

            }

        return localQuery;
    }

    public SQLQuery createSQLQuery(String sql, Object[] params) {
        Assert.hasText(sql);
        SQLQuery localQuery = getSession().createSQLQuery(sql);
        if (params != null)
            for (int i = 0; i < params.length; i++) {
                if (params[i] instanceof Date) {
                    localQuery.setTimestamp(i, (Date) params[i]);
                } else {
                    localQuery.setParameter(i, params[i]);
                }

            }

        return localQuery;
    }

    public List<T> findLimit(String sql, Object[] params, OrderParameters orderParams, int maxSize) {
        Assert.notNull(sql);

        if (maxSize <= 0) {
            return null;
        }

        sql = sql + (orderParams == null ? "" : orderParams.toString());
        Query localQuery = createQuery(sql, params);
        localQuery.setFirstResult(0);
        localQuery.setMaxResults(maxSize);
        return localQuery.list();
    }

    public Object findUnique(String sql, Object... params) {
        Query q = createQuery(sql, params);
        if (q != null) {
            return q.uniqueResult();
        }
        return null;
    }

    public Integer findInt(String sql, Object[] params) {
        return Integer.valueOf(findLong(sql, params).intValue());
    }

    public Long findLong(String sql, Object[] params) {
        Object count = findUnique(sql, params);

        return count == null ? 0L : (Long) count;
    }

    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }

    @Autowired
    public void setSessionFactory(@Qualifier("sessionFactory") SessionFactory paramSessionFactory) {
        this.sessionFactory = paramSessionFactory;
    }

    public Class<T> getEntityClass() {
        return this.entityClass;
    }

    public void setEntityClass(Class<T> paramClass) {
        this.entityClass = paramClass;
    }
}