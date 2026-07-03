package com.example.wgucapstone.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.wgucapstone.dao.CategoryDAO;
import com.example.wgucapstone.dao.ExcursionDAO;
import com.example.wgucapstone.dao.VacationDAO;
import com.example.wgucapstone.entities.Category;
import com.example.wgucapstone.entities.Excursion;
import com.example.wgucapstone.entities.Vacation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Vacation.class, Excursion.class, Category.class}, version = 2, exportSchema = false)
public abstract class VacationDatabase extends RoomDatabase {

    public abstract VacationDAO vacationDAO();
    public abstract ExcursionDAO excursionDAO();
    public abstract CategoryDAO categoryDAO();

    private static volatile VacationDatabase INSTANCE;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(4);

    // Default excursion categories — seeded once on first DB creation. Scalable
    // by design: new categories can be added to this table (see the "Add new
    // category" flow in ExcursionDetails) without touching any app code.
    private static final String[] DEFAULT_CATEGORIES = {
            "Sightseeing", "Adventure", "Relaxation", "Food & Dining", "Shopping"
    };

    private static final RoomDatabase.Callback SEED_CALLBACK = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                CategoryDAO categoryDAO = INSTANCE.categoryDAO();
                for (String name : DEFAULT_CATEGORIES) {
                    categoryDAO.insert(new Category(0, name));
                }
            });
        }
    };

    static VacationDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (VacationDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    VacationDatabase.class,
                                    "vacation_database")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .addCallback(SEED_CALLBACK)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

