package com.rancotech.tendtudo.repository.venda;

import com.rancotech.tendtudo.model.Venda;
import com.rancotech.tendtudo.repository.filter.VendaFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class VendaRepositoryImpl implements VendaRepositoryQuery {

    @PersistenceContext
    private EntityManager manager;

    @Override
    public Page<Venda> filtrar(VendaFilter vendaFilter, Pageable pageable) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Venda> criteria = builder.createQuery(Venda.class);
        Root<Venda> root = criteria.from(Venda.class);

        Predicate[] predicates = criarRestricoes(vendaFilter, builder, root);
        //criteria.multiselect(root.get("id"), root.get("data_venda"), root.get("status"), root.get("valor"));

        criteria.where(predicates);

        criteria.orderBy(builder.asc(root.get("id")));

        TypedQuery<Venda> query = manager.createQuery(criteria);

        adicionarRestricoesDePaginacao(query, pageable);

        return new PageImpl<>(query.getResultList(), pageable, total(vendaFilter));
    }

    private Predicate[] criarRestricoes(VendaFilter vendaFilter, CriteriaBuilder builder,
                                        Root<Venda> root) {
        List<Predicate> predicates = new ArrayList<>();

        if (!StringUtils.isEmpty(vendaFilter.getVendaDe())) {
            predicates.add(builder.greaterThanOrEqualTo(
                    root.get("data_venda"), vendaFilter.getVendaDe())
            );
        }

        int size = predicates.size();
        return predicates.toArray(new Predicate[size]);
    }

    private void adicionarRestricoesDePaginacao(TypedQuery<?> query, Pageable pageable) {
        int paginaAtual = pageable.getPageNumber();
        int totalRegistrosPorPagina = pageable.getPageSize();
        int primeiroRegistroDaPagina = paginaAtual * totalRegistrosPorPagina;
        query.setFirstResult(primeiroRegistroDaPagina);
        query.setMaxResults(totalRegistrosPorPagina);
    }

    private Long total(VendaFilter vendaFilter) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<Venda> root = criteria.from(Venda.class);

        Predicate[] predicates = criarRestricoes(vendaFilter, builder, root);
        criteria.where(predicates);

        criteria.select(builder.count(root));
        return manager.createQuery(criteria).getSingleResult();
    }

}
