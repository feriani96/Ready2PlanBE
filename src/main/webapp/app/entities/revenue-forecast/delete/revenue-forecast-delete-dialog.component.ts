import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IRevenueForecast } from '../revenue-forecast.model';
import { RevenueForecastService } from '../service/revenue-forecast.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './revenue-forecast-delete-dialog.component.html',
})
export class RevenueForecastDeleteDialogComponent {
  revenueForecast?: IRevenueForecast;

  constructor(protected revenueForecastService: RevenueForecastService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.revenueForecastService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
