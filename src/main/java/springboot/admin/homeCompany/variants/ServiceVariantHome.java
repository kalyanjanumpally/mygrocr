package springboot.admin.homeCompany.variants;

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
public class ServiceVariantHome {
	
	private DAOVariantHome dAOVariantsHome;
    private EntityManager entityManager;
	
	@Autowired
	public ServiceVariantHome(DAOVariantHome theVariantsDAOHome, EntityManager theEntityManager) {
		
		dAOVariantsHome = theVariantsDAOHome;
		entityManager = theEntityManager;
	}
	

	@Transactional
	public Boolean updateProductVariantByUnit(EntityProductVariantByUnitHome variant) {
		
		return dAOVariantsHome.updateProductVariantByUnit(variant);		
	}

	@Transactional
	public void updateProductInTenants(EntityProductVariantHome theProduct, Boolean bulkListChangeBool) throws Exception {
		
		dAOVariantsHome.updateProductInTenants(theProduct, bulkListChangeBool);
		
	}

	@Transactional
	public void productVariantToggleStatus(Integer variantId) {
		
		dAOVariantsHome.productVariantToggleStatus(variantId);
		
	}
	
	
	
	/*
	@Transactional
	public EntityProductVariantHome save(EntityProductVariantHome theProduct) {
		
		return dAOVariantsHome.save(theProduct);

	}

	@Transactional
	public ResponseProductsHome findAll(Integer itemsPerPage, Integer initialIndex) {
		
	//	ResponseProductsHome responseProducts = new ResponseProductsHome();
		
		return dAOVariantsHome.findAll(itemsPerPage, initialIndex);	
	}
	
	@Transactional
	public EntityProductVariantHome findById(Integer theId) {
		
		return dAOVariantsHome.findById(theId);
	}
	
	@Transactional
	public Boolean deleteById(int theId) {

		return dAOVariantsHome.deleteById(theId);
	}
	
	
	@Transactional
	public void updateProductDisplay(EntityProductVariantHome theProduct) {

		dAOVariantsHome.updateProductDisplay(theProduct);
	}
	
	@Transactional
	public ResponseProductsHome findProductsByCategory(Integer categoryId, Integer itemsPerPage, Integer initialIndex) {
		
		return dAOVariantsHome.findProductsByCategory(categoryId, itemsPerPage, initialIndex);	
	}
	
	@Transactional
	public ResponseProductsHome findProductsByBrand(Integer brandId, Integer itemsPerPage, Integer initialIndex) {
		
		return dAOVariantsHome.findProductsByBrand(brandId, itemsPerPage, initialIndex);	
	}
	
	
	@Transactional
	public void addProductCategoryEntry(Integer productId, List<Integer> categoryIds) {
		
		dAOVariantsHome.addProductCategoryEntry(productId, categoryIds);	
	}
	
	
	@Transactional
	public void deleteProductCategoryEntry(Integer productId, Integer categoryId) {
		
		dAOVariantsHome.deleteProductCategoryEntry(productId, categoryId);	
	}
	
	
    @Transactional
    public ResponseProductsHome searchProduct (String searchTerm, Integer startIndex, Integer itemsPerPage) { 

        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        
        try {
			fullTextEntityManager.createIndexer().startAndWait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

        QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(EntityProductVariantHome.class).get();
        Query luceneQuery = qb.keyword().fuzzy().withEditDistanceUpTo(1).withPrefixLength(1).onFields("productName")
                .matching(searchTerm).createQuery();

        javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, EntityProductVariantHome.class);

        // execute search
        
		jpaQuery.setFirstResult(startIndex);
		jpaQuery.setMaxResults(itemsPerPage);

        List<EntityProductVariantHome> dbProducts = null;
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
        	EntityProductVariantHome productItr = (EntityProductVariantHome) itr.next();
        	if(productItr.getProductDeleteStatus() != null && productItr.getProductDeleteStatus().equals(true)) {
        		itr.remove();
        	}       	
        }
        
        responseProducts.setProducts(dbProducts);        
        return responseProducts;
    }
    */

	

}
