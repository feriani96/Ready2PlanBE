import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ProductOrServiceComponent } from './list/product-or-service.component';
import { ProductOrServiceDetailComponent } from './detail/product-or-service-detail.component';
import { ProductOrServiceUpdateComponent } from './update/product-or-service-update.component';
import { ProductOrServiceDeleteDialogComponent } from './delete/product-or-service-delete-dialog.component';
import { ProductOrServiceRoutingModule } from './route/product-or-service-routing.module';

@NgModule({
  imports: [SharedModule, ProductOrServiceRoutingModule],
  declarations: [
    ProductOrServiceComponent,
    ProductOrServiceDetailComponent,
    ProductOrServiceUpdateComponent,
    ProductOrServiceDeleteDialogComponent,
  ],
})
export class ProductOrServiceModule {}
