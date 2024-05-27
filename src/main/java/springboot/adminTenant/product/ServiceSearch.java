package springboot.adminTenant.product;

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ServiceSearch {


    @Autowired
    private EntityManager entityManager;
    private DAOBatchesProduct dAOBatches;

 
    @Autowired
    public ServiceSearch(EntityManager entityManager, DAOBatchesProduct dAOBatches) {
     //   super();
        this.entityManager = entityManager;
        this.dAOBatches = dAOBatches;
    }


 /*   public void initializeHibernateSearch() {

        try {
            FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(centityManager);
            fullTextEntityManager.createIndexer().startAndWait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    } */

    @Transactional
    public ResponseProductsWithBatches searchProductWithBatches (String searchTerm, Integer startIndex, Integer itemsPerPage) { 

        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        
        try {
			fullTextEntityManager.createIndexer().startAndWait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(EntityProduct.class).get();
        Query luceneQuery = qb.keyword().fuzzy().withEditDistanceUpTo(1).withPrefixLength(1).onFields("productName")
                .matching(searchTerm).createQuery();

        javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, EntityProduct.class);

        // execute search
        
		jpaQuery.setFirstResult(startIndex);
		jpaQuery.setMaxResults(itemsPerPage);

        List<EntityProduct> dbProducts = null;
        try {
            dbProducts = jpaQuery.getResultList();
        } catch (NoResultException nre) {
            ;// do nothing
        }
        
        ResponseProductsWithBatches responseProducts = new ResponseProductsWithBatches();
        
		if(startIndex == 0) {
			Integer countResults = ((FullTextQuery) jpaQuery).getResultSize();
			responseProducts.setCountOfProducts( Long.valueOf(countResults));
		}
		
		List<EntityProduct> filteredDbProducts = new ArrayList<>();
		
		for (EntityProduct product : dbProducts) {
			
		    boolean variantDisplayON = false;
		    for (EntityProductVariantByUnitProduct variant : product.getProductVariantsByUnit()) {
		        if (variant.isVariantDisplay()) {
		            variantDisplayON = true;
		            break; // No need to check further if one variant is displayed
		        }
		    }
		    if (variantDisplayON) {
		    	filteredDbProducts.add(product); // Add the product to the filtered list
		    }
		}
        
    	List<DTOProductsAndBatches> dtos = dAOBatches.findBatches(filteredDbProducts);
    	responseProducts.setDtos(dtos);
        
        return responseProducts;
    }
    
    @Transactional
    public ResponseProducts searchProduct (String searchTerm, Integer startIndex, Integer itemsPerPage) { 

        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        
        try {
			fullTextEntityManager.createIndexer().startAndWait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(EntityProduct.class).get();
        Query luceneQuery = qb.keyword().fuzzy().withEditDistanceUpTo(1).withPrefixLength(1).onFields("productName")
                .matching(searchTerm).createQuery();

        javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, EntityProduct.class);

        // execute search
        
		jpaQuery.setFirstResult(startIndex);
		jpaQuery.setMaxResults(itemsPerPage);

        List<EntityProduct> dbProducts = null;
        try {
            dbProducts = jpaQuery.getResultList();
        } catch (NoResultException nre) {
            ;// do nothing
        }
        
        ResponseProducts responseProducts = new ResponseProducts();
        
		if(startIndex == 0) {
			Integer countResults = ((FullTextQuery) jpaQuery).getResultSize();
			responseProducts.setCountOfProducts( Long.valueOf(countResults));
		}
		
		List<EntityProduct> filteredDbProducts = new ArrayList<>();
		
		for (EntityProduct product : dbProducts) {
			
		    boolean variantDisplayON = false;
		    for (EntityProductVariantByUnitProduct variant : product.getProductVariantsByUnit()) {
		        if (variant.isVariantDisplay()) {
		            variantDisplayON = true;
		            break; // No need to check further if one variant is displayed
		        }
		    }
		    if (variantDisplayON) {
		    	filteredDbProducts.add(product); // Add the product to the filtered list
		    }
		}
        
		responseProducts.setDtos(filteredDbProducts);
        
        return responseProducts;
    }
    
    @Transactional
    public List<DTOProductsAndBatches> searchBatches (List<EntityProduct> products ) { 
    	
    	List<DTOProductsAndBatches> dTOs = dAOBatches.findBatches(products);
    	
    	return dTOs;
    }
    
    
}