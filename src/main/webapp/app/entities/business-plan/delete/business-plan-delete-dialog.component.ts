import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IBusinessPlan } from '../business-plan.model';
import { BusinessPlanService } from '../service/business-plan.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './business-plan-delete-dialog.component.html',
})
export class BusinessPlanDeleteDialogComponent {
  businessPlan?: IBusinessPlan;

  constructor(protected businessPlanService: BusinessPlanService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.businessPlanService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
