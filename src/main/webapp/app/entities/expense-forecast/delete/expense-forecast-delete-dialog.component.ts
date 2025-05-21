import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IExpenseForecast } from '../expense-forecast.model';
import { ExpenseForecastService } from '../service/expense-forecast.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './expense-forecast-delete-dialog.component.html',
})
export class ExpenseForecastDeleteDialogComponent {
  expenseForecast?: IExpenseForecast;

  constructor(protected expenseForecastService: ExpenseForecastService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.expenseForecastService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
