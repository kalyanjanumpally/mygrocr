package springboot.product;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.SerializationUtils;

@Repository
public class WebDAOBatchesProduct {

	private EntityManager entityManager;
	
	//set up constructor injection	
	public WebDAOBatchesProduct() {	
	}
	
	@Autowired
	public WebDAOBatchesProduct(EntityManager theEntityManager) {	
		this.entityManager = theEntityManager;
	}

	
	public List<WebDTOProductAndBatches> findBatches(List<WebEntityProduct> products) {

		List<WebDTOProductAndBatches> dTOs = new ArrayList<WebDTOProductAndBatches>();		
		
		for(WebEntityProduct product : products) {
			
			WebDTOProductAndBatches dTO = new WebDTOProductAndBatches();
			List<WebEntityBatchesProduct> allBatchesOfProduct = new ArrayList<>();
			
			dTO.setProduct(product);
			
			for(WebEntityProductVariantByUnit variant : product.getProductVariantsByUnit()){				
				List<WebEntityBatchesProduct> batches = find20LatestBatchesfromVariantId(variant.getVariantId());			
				for(WebEntityBatchesProduct batch : batches) {
					allBatchesOfProduct.add(batch);
				}
			}
			dTO.setBatchesData(allBatchesOfProduct);
			dTOs.add(dTO);
		}		  
		return dTOs;			
	}
	
	
	public List<WebEntityBatchesProduct> find20LatestBatchesfromVariantId(Integer variantId){
		
		Query theQuery = (Query) entityManager.createQuery("from WebEntityBatchesProduct where batchVariantId=:variantId AND batchPurSaleBool=:batchPurSaleBool AND currentQuantity is NOT NULL "
															+ " ORDER BY batchId DESC");
		theQuery.setParameter("variantId", variantId);
		theQuery.setParameter("batchPurSaleBool", 0);
		theQuery.setMaxResults(20);
		
		List<WebEntityBatchesProduct> dbBatches = theQuery.getResultList();
		
		Collections.reverse(dbBatches);
		
		return dbBatches;
	}

}
