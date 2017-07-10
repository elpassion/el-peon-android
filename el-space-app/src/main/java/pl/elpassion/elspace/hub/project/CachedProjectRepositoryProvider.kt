package pl.elpassion.elspace.hub.project

import android.preference.PreferenceManager
import com.elpassion.android.commons.sharedpreferences.createSharedPrefs
import com.elpassion.sharedpreferences.gsonadapter.GsonConverterAdapter
import pl.elpassion.elspace.common.ContextProvider
import pl.elpassion.elspace.common.Provider

object CachedProjectRepositoryProvider : Provider<CachedProjectRepository>({

    object : CachedProjectRepository {
        private val PROJECTS_KEY = "projects_key"
        private val defaultSharedPreferences = { PreferenceManager.getDefaultSharedPreferences(ContextProvider.get()) }
        private val repository = createSharedPrefs<List<Project>>(defaultSharedPreferences, GsonConverterAdapter())

        override fun saveProjects(projects: List<Project>) {
            repository.write(PROJECTS_KEY, projects)
        }

        override fun getPossibleProjects(): List<Project> {
            return repository.read(PROJECTS_KEY) ?: emptyList()
        }

        override fun hasProjects(): Boolean {
            return repository.contains(PROJECTS_KEY)
        }
    }
})