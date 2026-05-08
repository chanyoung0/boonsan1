package repository;

import model.underwriting.InsuranceApplication;

import java.util.ArrayList;
import java.util.List;

// 청약 메모리 저장소 — InsuranceApplication 객체를 List로 보관하고 조회를 제공
public class InsuranceApplicationRepository {

    private static final List<InsuranceApplication> store = new ArrayList<>();

    public static void save(InsuranceApplication application) {
        store.add(application);
    }

    public static InsuranceApplication findByApplicationId(String applicationId) {
        for (InsuranceApplication a : store) {
            if (a.getApplicationId().equals(applicationId)) return a;
        }
        return null;
    }

    public static List<InsuranceApplication> findAll() {
        return new ArrayList<>(store);
    }
}
