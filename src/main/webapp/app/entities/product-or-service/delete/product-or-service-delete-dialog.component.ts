import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IProductOrService } from '../product-or-service.model';
import { ProductOrServiceService } from '../service/product-or-service.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './product-or-service-delete-dialog.component.html',
})
export class ProductOrServiceDeleteDialogComponent {
  productOrService?: IProductOrService;

  constructor(protected productOrServiceService: ProductOrServiceService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.productOrServiceService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
