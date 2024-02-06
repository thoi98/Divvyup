package in.divvyup.persistence.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import com.google.common.collect.ImmutableMap;
import in.divvyup.persistence.PersistenceManager;
import in.divvyup.persistence.model.OTP;

@Repository
public class OTPDao {
    @Autowired
    @Qualifier ("mysql")
    private PersistenceManager persistenceManager;

    private static final String QUERY_BY_ID = "SELECT * FROM otp WHERE id=:id";

    public void save(OTP otp) {
        this.persistenceManager.save(otp);
    }

    public void update(OTP otp) {
        this.persistenceManager.update(otp);
    }

    public OTP getById(String id) {
        return this.persistenceManager.queryOne(QUERY_BY_ID, ImmutableMap.of("id", id), OTP.class);
    }
}
