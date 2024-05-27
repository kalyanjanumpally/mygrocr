package springboot.admin.homeCompany.products;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
public class ServiceHomeProduct {
	
	private DAOHomeProducts dAOProductsHome;
    private EntityManager entityManager;
	
	@Autowired
	public ServiceHomeProduct(DAOHomeProducts theProductsDAOHome, EntityManager theEntityManager) {
		
		dAOProductsHome = theProductsDAOHome;
		entityManager = theEntityManager;
	}
	
	@Transactional
	public EntityProductHome save(EntityProductHome theProduct) throws Exception {
		
		return dAOProductsHome.save(theProduct);

	}

	@Transactional
	public ResponseProductsHome findAll(Integer itemsPerPage, Integer initialIndex) {
		
	//	ResponseProductsHome responseProducts = new ResponseProductsHome();
		
		return dAOProductsHome.findAll(itemsPerPage, initialIndex);	
	}
	
	@Transactional
	public DTOProduct findById(Integer theId) {
		
		return dAOProductsHome.findById(theId);
	}
	
	@Transactional
	public void productActiveStatusToggle(Integer productId) {

		dAOProductsHome.productActiveStatusToggle(productId);
	}
	
	/*
	public EntityProductHome updateEditedProduct(EntityProductHome theProduct) {
		
		for(EntityProductVariantByUnitHome variant : theProduct.getProductVariantsByUnit()) {
			variant.setProduct(theProduct);
			updateProductVariantByUnit(variant);
		}
		
		
		return dAOProductsHome.updateEditedProduct(theProduct);		
	}
	*/
	
	/*
	@Transactional
	public void updateProductVariantByUnit(EntityProductVariantByUnitProductHome variant) {
		
		dAOProductsHome.updateProductVariantByUnit(variant);
		
	}
*/
	
	@Transactional
	public ResponseProductsHome getProductsByCategory(Integer categoryId, Integer itemsPerPage, Integer initialIndex) {
		
		return dAOProductsHome.getProductsByCategory(categoryId, itemsPerPage, initialIndex);	
	}
	
	@Transactional
	public ResponseProductsHome getProductsByBrand(Integer brandId, Integer itemsPerPage, Integer initialIndex) {
		
		return dAOProductsHome.getProductsByBrand(brandId, itemsPerPage, initialIndex);	
	}
	
	
    @Transactional
    public ResponseProductsHome searchProduct (String searchTerm, Integer startIndex, Integer itemsPerPage) { 

        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        
        try {
			fullTextEntityManager.createIndexer().startAndWait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

        QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(EntityProductHome.class).get();
        Query luceneQuery = qb.keyword().fuzzy().withEditDistanceUpTo(1).withPrefixLength(1).onFields("productName")
                .matching(searchTerm).createQuery();

        javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, EntityProductHome.class);

        // execute search
        
		jpaQuery.setFirstResult(startIndex);
		jpaQuery.setMaxResults(itemsPerPage);

        List<EntityProductHome> dbProducts = null;
        try {
            dbProducts = jpaQuery.getResultList();
        } catch (NoResultException nre) {
            ;// do nothing
        }
        
        ResponseProductsHome responseProducts = new ResponseProductsHome();
        
		if(startIndex == 0) {
			Integer countResults = ((FullTextQuery) jpaQuery).getResultSize();
			responseProducts.setCountOfProducts( Long.valueOf(countResults));
		}
        
        // remove soft deleted products
        Iterator itr = dbProducts.iterator();
        
        while(itr.hasNext()) {
        	EntityProductHome productItr = (EntityProductHome) itr.next();
        	if(productItr.getProductDeleteStatus() != null && productItr.getProductDeleteStatus().equals(true)) {
        		itr.remove();
        	}       	
        }
        
        responseProducts.setProducts(dbProducts);        
        return responseProducts;
    }
    
    /*
	@Transactional
	public void addProductCategoryEntry(Integer productId, List<Integer> categoryIds) {
		
		dAOProductsHome.addProductCategoryEntry(productId, categoryIds);	
	}
	
	
	@Transactional
	public void deleteProductCategoryEntry(Integer productId, Integer categoryId) {
		
		dAOProductsHome.deleteProductCategoryEntry(productId, categoryId);	
	}
	
	@Transactional
	public void updateProductDisplay(EntityProductHome theProduct) {

		dAOProductsHome.updateProductDisplay(theProduct);
	}
    */

	

}
