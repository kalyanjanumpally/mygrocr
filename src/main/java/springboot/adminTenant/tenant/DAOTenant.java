package springboot.adminTenant.tenant;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import springboot.properties.TenantDbProperties;



@Repository
public class DAOTenant  {

	private EntityManager entityManager;
	private String tenantDbUrl;
	private String tenantDbUsername;
	private String tenantDbPassword;
	
	//set up constructor injection
	
	public DAOTenant() {	
	}
	
	@Autowired
	public DAOTenant(EntityManager theEntityManager, TenantDbProperties tenantDbProperties) {
		
		this.entityManager = theEntityManager;
		this.tenantDbUrl = tenantDbProperties.getTenantsDbUrl();
		this.tenantDbUsername = tenantDbProperties.getTenantDbUsername();
		this.tenantDbPassword = tenantDbProperties.getTenantDbPassword();
	}
	
	public void updateTenantDetailsByTenant(DTOTenantDetails tenantDetails) throws Exception {
		/*
		Session currentSession = entityManager.unwrap(Session.class);	
		
		Query<EntityTenant> theQuery = currentSession.createQuery("from EntityTenant WHERE tenantUrl =:tenantUrl");
		theQuery.setParameter("tenantUrl", tenantDetails.getTenantUrl());
		
		EntityTenant dbTenant = theQuery.getSingleResult();	
		
		dbTenant.setShopOpenTime(tenantDetails.getShopOpenTime());
		dbTenant.setShopCloseTime(tenantDetails.getShopCloseTime());
		dbTenant.setTenantOpen(tenantDetails.getTenantOpen());
		
		entityManager.merge(dbTenant);	
		*/
		
        try {       	
        	String url = "jdbc:mysql://" + tenantDbUrl + ":3306/home_company?useSSL=false&serverTimezone=UTC&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false&allowPublicKeyRetrieval=true";
    		String username = tenantDbUsername;
    		String password = tenantDbPassword;
    		Connection connection = DriverManager.getConnection(url, username, password);
    		
            Statement statement = connection.createStatement();
            
            String updateTenantDetails = "UPDATE tenants SET "
            							+ "shop_open_time = \"" + tenantDetails.getShopOpenTime() + "\", "
            							+ "shop_close_time = \"" + tenantDetails.getShopCloseTime() + "\", " 
            							+ "tenant_open = " + tenantDetails.getTenantOpen() 	            							
            							+ " WHERE tenant_id = " + tenantDetails.getTenantId() + ";";

            
               statement.executeUpdate(updateTenantDetails);
        }
        catch(Exception e) {
        	throw e;
        }
	}

	public DTOTenantDetails getTenantDetailsFromUrl(String tenantUrl) throws Exception {
		
		DTOTenantDetails dtoTenantDetails = new DTOTenantDetails();
		
		
        try {       	
        	String url = "jdbc:mysql://" + tenantDbUrl  + ":3306/home_company?useSSL=false&serverTimezone=UTC&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false&allowPublicKeyRetrieval=true";
    		String username = tenantDbUsername;
    		String password = tenantDbPassword;
    		Connection connection = DriverManager.getConnection(url, username, password);
    		
            Statement statement = connection.createStatement();
            
            String getTenantData = "SELECT tenant_id, tenant_open, shop_open_time, shop_close_time FROM tenants WHERE tenant_url = \"" + tenantUrl + "\" LIMIT 1;";
            
            ResultSet resultSet = statement.executeQuery(getTenantData);
            
            if(resultSet.next()) {
            	dtoTenantDetails.setTenantOpen(resultSet.getBoolean("tenant_open"));
            	String shopOpenTime = resultSet.getString("shop_open_time");
            	String shopCloseTime = resultSet.getString("shop_close_time");
            	           	
            	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            	dtoTenantDetails.setShopOpenTime(LocalTime.parse(shopOpenTime, formatter));
            	dtoTenantDetails.setShopCloseTime(LocalTime.parse(shopCloseTime, formatter));
            	dtoTenantDetails.setTenantId(resultSet.getInt("tenant_id"));
            	
            }
            
        }
        catch(Exception e) {
        	throw e;
        }
		return dtoTenantDetails;		
	
	}

}
