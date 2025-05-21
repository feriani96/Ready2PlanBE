import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { EntrepriseComponent } from './list/entreprise.component';
import { EntrepriseDetailComponent } from './detail/entreprise-detail.component';
import { EntrepriseUpdateComponent } from './update/entreprise-update.component';
import { EntrepriseDeleteDialogComponent } from './delete/entreprise-delete-dialog.component';
import { EntrepriseRoutingModule } from './route/entreprise-routing.module';

@NgModule({
  imports: [SharedModule, EntrepriseRoutingModule],
  declarations: [EntrepriseComponent, EntrepriseDetailComponent, EntrepriseUpdateComponent, EntrepriseDeleteDialogComponent],
})
export class EntrepriseModule {}
