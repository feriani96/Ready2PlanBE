import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { BusinessPlanComponent } from './list/business-plan.component';
import { BusinessPlanDetailComponent } from './detail/business-plan-detail.component';
import { BusinessPlanUpdateComponent } from './update/business-plan-update.component';
import { BusinessPlanDeleteDialogComponent } from './delete/business-plan-delete-dialog.component';
import { BusinessPlanRoutingModule } from './route/business-plan-routing.module';

@NgModule({
  imports: [SharedModule, BusinessPlanRoutingModule],
  declarations: [BusinessPlanComponent, BusinessPlanDetailComponent, BusinessPlanUpdateComponent, BusinessPlanDeleteDialogComponent],
})
export class BusinessPlanModule {}
