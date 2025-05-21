import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ProductOrServiceFormService, ProductOrServiceFormGroup } from './product-or-service-form.service';
import { IProductOrService } from '../product-or-service.model';
import { ProductOrServiceService } from '../service/product-or-service.service';
import { IBusinessPlan } from 'app/entities/business-plan/business-plan.model';
import { BusinessPlanService } from 'app/entities/business-plan/service/business-plan.service';

@Component({
  selector: 'jhi-product-or-service-update',
  templateUrl: './product-or-service-update.component.html',
})
export class ProductOrServiceUpdateComponent implements OnInit {
  isSaving = false;
  productOrService: IProductOrService | null = null;

  businessPlansSharedCollection: IBusinessPlan[] = [];

  editForm: ProductOrServiceFormGroup = this.productOrServiceFormService.createProductOrServiceFormGroup();

  constructor(
    protected productOrServiceService: ProductOrServiceService,
    protected productOrServiceFormService: ProductOrServiceFormService,
    protected businessPlanService: BusinessPlanService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareBusinessPlan = (o1: IBusinessPlan | null, o2: IBusinessPlan | null): boolean =>
    this.businessPlanService.compareBusinessPlan(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ productOrService }) => {
      this.productOrService = productOrService;
      if (productOrService) {
        this.updateForm(productOrService);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const productOrService = this.productOrServiceFormService.getProductOrService(this.editForm);
    if (productOrService.id !== null) {
      this.subscribeToSaveResponse(this.productOrServiceService.update(productOrService));
    } else {
      this.subscribeToSaveResponse(this.productOrServiceService.create(productOrService));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProductOrService>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(productOrService: IProductOrService): void {
    this.productOrService = productOrService;
    this.productOrServiceFormService.resetForm(this.editForm, productOrService);

    this.businessPlansSharedCollection = this.businessPlanService.addBusinessPlanToCollectionIfMissing<IBusinessPlan>(
      this.businessPlansSharedCollection,
      productOrService.businessPlan
    );
  }

  protected loadRelationshipsOptions(): void {
    this.businessPlanService
      .query()
      .pipe(map((res: HttpResponse<IBusinessPlan[]>) => res.body ?? []))
      .pipe(
        map((businessPlans: IBusinessPlan[]) =>
          this.businessPlanService.addBusinessPlanToCollectionIfMissing<IBusinessPlan>(businessPlans, this.productOrService?.businessPlan)
        )
      )
      .subscribe((businessPlans: IBusinessPlan[]) => (this.businessPlansSharedCollection = businessPlans));
  }
}
