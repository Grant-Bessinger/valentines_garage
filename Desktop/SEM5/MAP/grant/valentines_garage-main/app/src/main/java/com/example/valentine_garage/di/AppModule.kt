package com.example.valentine_garage.di

import com.example.valentine_garage.database.dao.*
import com.example.valentine_garage.service.ManagerService
import com.example.valentine_garage.ui.repositories.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideTruckRepository(truckDao: TruckDao, managerService: ManagerService): TruckRepository {
        return TruckRepository(truckDao, managerService)
    }

    @Provides
    fun provideClientRepository(clientDao: ClientDao, managerService: ManagerService): ClientRepository {
        return ClientRepository(clientDao, managerService)
    }

    @Provides
    fun provideVehicleRepository(vehicleDao: VehicleDao, managerService: ManagerService): VehicleRepository {
        return VehicleRepository(vehicleDao, managerService)
    }

    @Provides
    fun provideJobRepository(jobDao: JobDao, managerService: ManagerService): JobRepository {
        return JobRepository(jobDao, managerService)
    }

    @Provides
    fun provideInvoiceRepository(invoiceDao: InvoiceDao, managerService: ManagerService): InvoiceRepository {
        return InvoiceRepository(invoiceDao, managerService)
    }
}
