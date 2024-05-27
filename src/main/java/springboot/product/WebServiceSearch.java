package springboot.product;

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import springboot.adminTenant.product.EntityProduct;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class WebServiceSearch {


    @Autowired
    private EntityManager entityManager;
    private WebDAOBatchesProduct dAOBatches;

 
    @Autowired
    public WebServiceSearch(EntityManager entityManager, WebDAOBatchesProduct dAOBatches) {
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
    public WebResponseProducts searchProduct (String searchTerm, Integer startIndex, Integer itemsPerPage) { 

        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        
        try {
			fullTextEntityManager.createIndexer().startAndWait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(WebEntityProduct.class).get();
        Query luceneQuery = qb.keyword().fuzzy().withEditDistanceUpTo(1).withPrefixLength(1).onFields("productName")
                .matching(searchTerm).createQuery();

        javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, WebEntityProduct.class);

        // execute search
        
		jpaQuery.setFirstResult(startIndex);
		jpaQuery.setMaxResults(itemsPerPage);

        List<WebEntityProduct> dbProducts = null;
        try {
            dbProducts = jpaQuery.getResultList();
        } catch (NoResultException nre) {
            ;// do nothing
        }
        
        WebResponseProducts webResponseProducts = new WebResponseProducts();
        
		if(startIndex == 0) {
			Integer countResults = ((FullTextQuery) jpaQuery).getResultSize();
			webResponseProducts.setCountOfProducts( Long.valueOf(countResults));
		}
        
		/*
        // remove soft deleted products
        Iterator itr = dbProducts.iterator();
        
        while(itr.hasNext()) {
        	
        	WebEntityProduct productItr = (WebEntityProduct) itr.next();    	
        	
        	if((productItr.getProductDeleteStatus() != null && productItr.getProductDeleteStatus().equals(true)) || 
        			( productItr.isDisplay() == false )) {
        		itr.remove();
        	}       	
        }
        */
        
		List<WebEntityProduct> filteredDbProducts = new ArrayList<>();
		
		for (WebEntityProduct product : dbProducts) {
			
		    boolean variantDisplayON = false;
		    for (WebEntityProductVariantByUnit variant : product.getProductVariantsByUnit()) {
		        if (variant.isVariantDisplay()) {
		            variantDisplayON = true;
		            break; // No need to check further if one variant is displayed
		        }
		    }
		    if (variantDisplayON) {
		    	filteredDbProducts.add(product); // Add the product to the filtered list
		    }
		}

        
    	List<WebDTOProductAndBatches> dtos = dAOBatches.findBatches(filteredDbProducts);
    	webResponseProducts.setDtos(dtos);
        
        return webResponseProducts;
    }
    
    @Transactional
    public List<WebDTOProductAndBatches> searchBatches (List<WebEntityProduct> products ) { 
    	
    	List<WebDTOProductAndBatches> dTOs = dAOBatches.findBatches(products);
    	
    	return dTOs;
    }

/*
	public List<WebEntityBatchesProduct> getBatchesQuantity(Integer productId) {
		
		return dAOBatches.findAllBatchesfromProductId(productId);

	}
	*/
    
    
}