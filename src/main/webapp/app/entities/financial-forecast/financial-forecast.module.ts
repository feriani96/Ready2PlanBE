import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { FinancialForecastComponent } from './list/financial-forecast.component';
import { FinancialForecastDetailComponent } from './detail/financial-forecast-detail.component';
import { FinancialForecastUpdateComponent } from './update/financial-forecast-update.component';
import { FinancialForecastDeleteDialogComponent } from './delete/financial-forecast-delete-dialog.component';
import { FinancialForecastRoutingModule } from './route/financial-forecast-routing.module';

@NgModule({
  imports: [SharedModule, FinancialForecastRoutingModule],
  declarations: [
    FinancialForecastComponent,
    FinancialForecastDetailComponent,
    FinancialForecastUpdateComponent,
    FinancialForecastDeleteDialogComponent,
  ],
})
export class FinancialForecastModule {}
