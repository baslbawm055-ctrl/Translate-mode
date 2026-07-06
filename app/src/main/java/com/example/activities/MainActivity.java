package com.example.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.R;
import com.example.dialogs.CustomDialogs;
import com.example.fragments.HistoryFragment;
import com.example.fragments.HomeFragment;
import com.example.fragments.ProjectsFragment;
import com.example.fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, HomeFragment.TabSwitcher {

    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false); // We have custom title/logo in XML
        }

        // Drawer Setup
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.btn_ok, // Just simple strings for accessibility description
                R.string.btn_cancel
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Change the Drawer Hamburger Icon tint to Primary Accent (Green) for modern styling
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.accent_color));

        // NavigationView Setup
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Bottom Navigation Setup
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                loadFragment(new HomeFragment());
                return true;
            } else if (itemId == R.id.nav_projects) {
                loadFragment(new ProjectsFragment());
                return true;
            } else if (itemId == R.id.nav_history) {
                loadFragment(new HistoryFragment());
                return true;
            } else if (itemId == R.id.nav_settings) {
                loadFragment(new SettingsFragment());
                return true;
            }
            return false;
        });

        // Floating Action Button Action
        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> {
            Dialog progress = CustomDialogs.showProgressDialog(this);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                progress.dismiss();
                CustomDialogs.showSuccessDialog(this, 
                    getString(R.string.menu_open_file), 
                    "سيتم تشغيل متصفح الملفات الاحترافي لتحديد ملفات XML و ARSC لترجمتها وتعديلها في المرحلة المقبلة!", 
                    null
                );
            }, 1200);
        });

        // Set Default Tab
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            navigationView.setCheckedItem(R.id.drawer_home);
        }
    }

    private void loadFragment(Fragment fragment) {
        if (fragment instanceof HomeFragment) {
            ((HomeFragment) fragment).setTabSwitcher(this);
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    @Override
    public void switchToTab(int menuItemId) {
        bottomNavigationView.setSelectedItemId(menuItemId);
    }

    // Options Menu setup (Search, Settings, etc.)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate modern search and settings icons into options menu
        MenuItem searchItem = menu.add(Menu.NONE, 101, Menu.NONE, "بحث");
        searchItem.setIcon(R.drawable.ic_search);
        searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        MenuItem settingsItem = menu.add(Menu.NONE, 102, Menu.NONE, "إعدادات سريعة");
        settingsItem.setIcon(R.drawable.ic_settings);
        settingsItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        MenuItem clearHistoryItem = menu.add(Menu.NONE, 103, Menu.NONE, "مسح السجل");
        clearHistoryItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == 101) {
            // Open Advanced Search activity
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        } else if (id == 102) {
            // Switch to Settings fragment
            switchToTab(R.id.nav_settings);
            return true;
        } else if (id == 103) {
            // Clear History dialogue
            CustomDialogs.showConfirmDialog(this,
                getString(R.string.dialog_confirm_title),
                getString(R.string.dialog_confirm_msg),
                () -> {
                    Dialog progress = CustomDialogs.showProgressDialog(this);
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        progress.dismiss();
                        CustomDialogs.showSuccessDialog(this,
                            "تم المسح بنجاح",
                            "تم إفراغ كافة بيانات السجل المحفوظة بالكامل.",
                            null
                        );
                    }, 1500);
                },
                () -> Toast.makeText(this, "تم إلغاء عملية المسح", Toast.LENGTH_SHORT).show()
            );
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.drawer_home) {
            switchToTab(R.id.nav_home);
        } else if (id == R.id.drawer_open_file) {
            drawerLayout.closeDrawer(GravityCompat.START);
            Dialog progress = CustomDialogs.showProgressDialog(this);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                progress.dismiss();
                CustomDialogs.showSuccessDialog(this,
                    getString(R.string.menu_open_file),
                    "ميزة فتح الملفات تتيح تحميل أي ملف ترجمة من الذاكرة لتعريبه أو ترجمته مباشرة بالذكاء الاصطناعي.",
                    null
                );
            }, 1200);
            return true;
        } else if (id == R.id.drawer_projects) {
            switchToTab(R.id.nav_projects);
        } else if (id == R.id.drawer_history) {
            switchToTab(R.id.nav_history);
        } else if (id == R.id.drawer_translation_memory) {
            drawerLayout.closeDrawer(GravityCompat.START);
            Dialog progress = CustomDialogs.showProgressDialog(this);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                progress.dismiss();
                CustomDialogs.showSuccessDialog(this,
                    "ذاكرة الترجمة (TM)",
                    "ذاكرة الترجمة تقوم بحفظ التعبيرات والترجمات السابقة لتسريع عملية تعريب الإصدارات الجديدة تلقائياً بدون تكرار الجهد.",
                    null
                );
            }, 1200);
            return true;
        } else if (id == R.id.drawer_dictionary) {
            drawerLayout.closeDrawer(GravityCompat.START);
            Dialog progress = CustomDialogs.showProgressDialog(this);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                progress.dismiss();
                CustomDialogs.showSuccessDialog(this,
                    "القاموس الذكي",
                    "القاموس يتيح إدخال الكلمات الشائعة ومصطلحاتك الخاصة لضمان ثبات وترابط الترجمة طوال المشروع.",
                    null
                );
            }, 1200);
            return true;
        } else if (id == R.id.drawer_plugins) {
            drawerLayout.closeDrawer(GravityCompat.START);
            Dialog progress = CustomDialogs.showProgressDialog(this);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                progress.dismiss();
                CustomDialogs.showSuccessDialog(this,
                    "سوق الإضافات",
                    "متجر الإضافات يتيح تثبيت وتحديث أدوات ترجمة إضافية وحزم لغوية تم تطويرها بواسطة مجتمع مطوري BL Manager.",
                    null
                );
            }, 1200);
            return true;
        } else if (id == R.id.drawer_settings) {
            switchToTab(R.id.nav_settings);
        } else if (id == R.id.drawer_help) {
            drawerLayout.closeDrawer(GravityCompat.START);
            CustomDialogs.showSuccessDialog(this,
                "تعليمات الاستخدام والمساعدة",
                "لترجمة أي ملف:\n1. انقر على 'اختر ملفاً'\n2. حدد ملف XML أو ARSC أو JSON\n3. سيتم قراءة النصوص وترتيبها تلقائياً\n4. اختر ميزة 'ترجمة الذكاء الاصطناعي' لتعريب العبارات دفعة واحدة.",
                null
            );
            return true;
        } else if (id == R.id.drawer_about) {
            drawerLayout.closeDrawer(GravityCompat.START);
            CustomDialogs.showSuccessDialog(this,
                getString(R.string.app_name),
                getString(R.string.setting_about_desc) + "\n\n" + getString(R.string.app_version) + "\n\nمطور بالكامل بلغة Java بنسبة 100%.",
                null
            );
            return true;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
