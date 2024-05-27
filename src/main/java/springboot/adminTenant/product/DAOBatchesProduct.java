package springboot.adminTenant.product;


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
public class DAOBatchesProduct {

	private EntityManager entityManager;
	
	//set up constructor injection	
	public DAOBatchesProduct() {	
	}
	
	@Autowired
	public DAOBatchesProduct(EntityManager theEntityManager) {	
		this.entityManager = theEntityManager;
	}

	public List<DTOProductsAndBatches> findBatches(List<EntityProduct> products) {
		
		List<DTOProductsAndBatches> dTOs = new ArrayList<DTOProductsAndBatches>();		
		
		for(EntityProduct product : products) {
			
			DTOProductsAndBatches dTO = new DTOProductsAndBatches();
			List<EntityBatchesProduct> allBatchesOfProduct = new ArrayList<>();
			
			dTO.setProduct(product);
			
			for(EntityProductVariantByUnitProduct variant : product.getProductVariantsByUnit()){				
				List<EntityBatchesProduct> batches = findBatchesfromVariantId(variant.getVariantId());			
				for(EntityBatchesProduct batch : batches) {
					allBatchesOfProduct.add(batch);
				}
			}
			dTO.setBatchesData(allBatchesOfProduct);
			dTOs.add(dTO);
		}		  
		return dTOs;			
	}
	
	
	public List<EntityBatchesProduct> findBatchesfromVariantId(Integer variantId){
				
		Query theQuery = (Query) entityManager.createQuery("from EntityBatchesProduct where batchVariantId=:variantId AND batchPurSaleBool=:batchPurSaleBool "
															+ "AND currentQuantity is NOT NULL ORDER BY batchId DESC");
		theQuery.setParameter("variantId", variantId);
		theQuery.setParameter("batchPurSaleBool", 0);
		theQuery.setMaxResults(20);
		
		List<EntityBatchesProduct> dbBatches = theQuery.getResultList();
		
		Collections.reverse(dbBatches);
				
		return dbBatches;
					
	}

}
