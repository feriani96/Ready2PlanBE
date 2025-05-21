import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProductOrServiceFormService } from './product-or-service-form.service';
import { ProductOrServiceService } from '../service/product-or-service.service';
import { IProductOrService } from '../product-or-service.model';
import { IBusinessPlan } from 'app/entities/business-plan/business-plan.model';
import { BusinessPlanService } from 'app/entities/business-plan/service/business-plan.service';

import { ProductOrServiceUpdateComponent } from './product-or-service-update.component';

describe('ProductOrService Management Update Component', () => {
  let comp: ProductOrServiceUpdateComponent;
  let fixture: ComponentFixture<ProductOrServiceUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let productOrServiceFormService: ProductOrServiceFormService;
  let productOrServiceService: ProductOrServiceService;
  let businessPlanService: BusinessPlanService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProductOrServiceUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ProductOrServiceUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProductOrServiceUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    productOrServiceFormService = TestBed.inject(ProductOrServiceFormService);
    productOrServiceService = TestBed.inject(ProductOrServiceService);
    businessPlanService = TestBed.inject(BusinessPlanService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call BusinessPlan query and add missing value', () => {
      const productOrService: IProductOrService = { id: 'CBA' };
      const businessPlan: IBusinessPlan = { id: 'c0ce437c-77ce-4cc4-a861-66d976a4130b' };
      productOrService.businessPlan = businessPlan;

      const businessPlanCollection: IBusinessPlan[] = [{ id: '2fa7a6c0-7bb7-4f31-a979-bd7de11f50a3' }];
      jest.spyOn(businessPlanService, 'query').mockReturnValue(of(new HttpResponse({ body: businessPlanCollection })));
      const additionalBusinessPlans = [businessPlan];
      const expectedCollection: IBusinessPlan[] = [...additionalBusinessPlans, ...businessPlanCollection];
      jest.spyOn(businessPlanService, 'addBusinessPlanToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ productOrService });
      comp.ngOnInit();

      expect(businessPlanService.query).toHaveBeenCalled();
      expect(businessPlanService.addBusinessPlanToCollectionIfMissing).toHaveBeenCalledWith(
        businessPlanCollection,
        ...additionalBusinessPlans.map(expect.objectContaining)
      );
      expect(comp.businessPlansSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const productOrService: IProductOrService = { id: 'CBA' };
      const businessPlan: IBusinessPlan = { id: '9b6b7acd-3011-4b96-bf6a-b3300377d712' };
      productOrService.businessPlan = businessPlan;

      activatedRoute.data = of({ productOrService });
      comp.ngOnInit();

      expect(comp.businessPlansSharedCollection).toContain(businessPlan);
      expect(comp.productOrService).toEqual(productOrService);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProductOrService>>();
      const productOrService = { id: 'ABC' };
      jest.spyOn(productOrServiceFormService, 'getProductOrService').mockReturnValue(productOrService);
      jest.spyOn(productOrServiceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ productOrService });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: productOrService }));
      saveSubject.complete();

      // THEN
      expect(productOrServiceFormService.getProductOrService).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(productOrServiceService.update).toHaveBeenCalledWith(expect.objectContaining(productOrService));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProductOrService>>();
      const productOrService = { id: 'ABC' };
      jest.spyOn(productOrServiceFormService, 'getProductOrService').mockReturnValue({ id: null });
      jest.spyOn(productOrServiceService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ productOrService: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: productOrService }));
      saveSubject.complete();

      // THEN
      expect(productOrServiceFormService.getProductOrService).toHaveBeenCalled();
      expect(productOrServiceService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProductOrService>>();
      const productOrService = { id: 'ABC' };
      jest.spyOn(productOrServiceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ productOrService });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(productOrServiceService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareBusinessPlan', () => {
      it('Should forward to businessPlanService', () => {
        const entity = { id: 'ABC' };
        const entity2 = { id: 'CBA' };
        jest.spyOn(businessPlanService, 'compareBusinessPlan');
        comp.compareBusinessPlan(entity, entity2);
        expect(businessPlanService.compareBusinessPlan).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
