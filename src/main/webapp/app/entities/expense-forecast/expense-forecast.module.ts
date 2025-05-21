import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ExpenseForecastComponent } from './list/expense-forecast.component';
import { ExpenseForecastDetailComponent } from './detail/expense-forecast-detail.component';
import { ExpenseForecastUpdateComponent } from './update/expense-forecast-update.component';
import { ExpenseForecastDeleteDialogComponent } from './delete/expense-forecast-delete-dialog.component';
import { ExpenseForecastRoutingModule } from './route/expense-forecast-routing.module';

@NgModule({
  imports: [SharedModule, ExpenseForecastRoutingModule],
  declarations: [
    ExpenseForecastComponent,
    ExpenseForecastDetailComponent,
    ExpenseForecastUpdateComponent,
    ExpenseForecastDeleteDialogComponent,
  ],
})
export class ExpenseForecastModule {}
