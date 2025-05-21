import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { RevenueForecastComponent } from './list/revenue-forecast.component';
import { RevenueForecastDetailComponent } from './detail/revenue-forecast-detail.component';
import { RevenueForecastUpdateComponent } from './update/revenue-forecast-update.component';
import { RevenueForecastDeleteDialogComponent } from './delete/revenue-forecast-delete-dialog.component';
import { RevenueForecastRoutingModule } from './route/revenue-forecast-routing.module';

@NgModule({
  imports: [SharedModule, RevenueForecastRoutingModule],
  declarations: [
    RevenueForecastComponent,
    RevenueForecastDetailComponent,
    RevenueForecastUpdateComponent,
    RevenueForecastDeleteDialogComponent,
  ],
})
export class RevenueForecastModule {}
