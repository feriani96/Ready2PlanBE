import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IFinancialForecast } from '../financial-forecast.model';
import { FinancialForecastService } from '../service/financial-forecast.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './financial-forecast-delete-dialog.component.html',
})
export class FinancialForecastDeleteDialogComponent {
  financialForecast?: IFinancialForecast;

  constructor(protected financialForecastService: FinancialForecastService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.financialForecastService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
