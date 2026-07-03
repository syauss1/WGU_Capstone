package com.example.wgucapstone.database;

import android.app.Application;

import com.example.wgucapstone.dao.CategoryDAO;
import com.example.wgucapstone.dao.ExcursionDAO;
import com.example.wgucapstone.dao.VacationDAO;
import com.example.wgucapstone.entities.Category;
import com.example.wgucapstone.entities.Excursion;
import com.example.wgucapstone.entities.Vacation;

import java.util.List;

public class Repository {

    private final VacationDAO mVacationDAO;
    private final ExcursionDAO mExcursionDAO;
    private final CategoryDAO mCategoryDAO;

    public Repository(Application application) {
        VacationDatabase db = VacationDatabase.getDatabase(application);
        mVacationDAO  = db.vacationDAO();
        mExcursionDAO = db.excursionDAO();
        mCategoryDAO  = db.categoryDAO();
    }

    // ── Vacations ─────────────────────────────────────────────────────────────

    public List<Vacation> getmAllVacations() {
        return mVacationDAO.getAllVacations();
    }

    public List<Vacation> searchVacations(String query) {
        return mVacationDAO.searchVacations(query);
    }

    public void insert(Vacation vacation) {
        VacationDatabase.databaseWriteExecutor.execute(() -> mVacationDAO.insert(vacation));
    }

    public void update(Vacation vacation) {
        VacationDatabase.databaseWriteExecutor.execute(() -> mVacationDAO.update(vacation));
    }

    public void delete(Vacation vacation) {
        VacationDatabase.databaseWriteExecutor.execute(() -> mVacationDAO.delete(vacation));
    }

    // ── Excursions ────────────────────────────────────────────────────────────

    public List<Excursion> getAllExcursions() {
        return mExcursionDAO.getAllExcursions();
    }

    public List<Excursion> getExcursionsByVacation(int vacationID) {
        return mExcursionDAO.getExcursionsByVacation(vacationID);
    }

    public void insert(Excursion excursion) {
        VacationDatabase.databaseWriteExecutor.execute(() -> mExcursionDAO.insert(excursion));
    }

    public void update(Excursion excursion) {
        VacationDatabase.databaseWriteExecutor.execute(() -> mExcursionDAO.update(excursion));
    }

    public void delete(Excursion excursion) {
        VacationDatabase.databaseWriteExecutor.execute(() -> mExcursionDAO.delete(excursion));
    }

    // ── Categories ────────────────────────────────────────────────────────────

    public List<Category> getAllCategories() {
        return mCategoryDAO.getAllCategories();
    }

    public void insertCategory(Category category) {
        VacationDatabase.databaseWriteExecutor.execute(() -> mCategoryDAO.insert(category));
    }
}