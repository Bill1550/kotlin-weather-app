package com.loneoaktech.test.weatherapp.di

import com.loneoaktech.test.weatherapp.viewmodel.LocationViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import android.arch.lifecycle.ViewModel
import dagger.MapKey
import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy
import kotlin.reflect.KClass


/**
 * The module specifications for the ViewModels.
 * Builds a mapping table used the the factory to create & inject them.
 *
 * Created by BillH on 9/21/2017.
 */
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)


//---------- Not currently used dur to an internal compiler error in KAPT.
// created separate ViewModel factories for each VM using constructor injection.

@Module
abstract class ViewModelModule {
//    @Binds
//    @IntoMap
//    @ViewModelKey(LocationViewModel::class)
    abstract fun bindLocationViewModel(locationViewModel: LocationViewModel)

//    @Binds
//    @IntoMap
//    @ViewModelKey(ForecastViewModel::class)
}
