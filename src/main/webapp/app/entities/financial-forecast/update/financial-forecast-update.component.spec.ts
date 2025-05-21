import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { FinancialForecastFormService } from './financial-forecast-form.service';
import { FinancialForecastService } from '../service/financial-forecast.service';
import { IFinancialForecast } from '../financial-forecast.model';
import { IBusinessPlan } from 'app/entities/business-plan/business-plan.model';
import { BusinessPlanService } from 'app/entities/business-plan/service/business-plan.service';

import { FinancialForecastUpdateComponent } from './financial-forecast-update.component';

describe('FinancialForecast Management Update Component', () => {
  let comp: FinancialForecastUpdateComponent;
  let fixture: ComponentFixture<FinancialForecastUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let financialForecastFormService: FinancialForecastFormService;
  let financialForecastService: FinancialForecastService;
  let businessPlanService: BusinessPlanService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [FinancialForecastUpdateComponent],
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
      .overrideTemplate(FinancialForecastUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FinancialForecastUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    financialForecastFormService = TestBed.inject(FinancialForecastFormService);
    financialForecastService = TestBed.inject(FinancialForecastService);
    businessPlanService = TestBed.inject(BusinessPlanService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call businessPlan query and add missing value', () => {
      const financialForecast: IFinancialForecast = { id: 'CBA' };
      const businessPlan: IBusinessPlan = { id: 'df1bf72e-a917-4969-bb30-a66a34262b2e' };
      financialForecast.businessPlan = businessPlan;

      const businessPlanCollection: IBusinessPlan[] = [{ id: '5b331f01-fc4d-4ae1-a9d8-78d9884af4bc' }];
      jest.spyOn(businessPlanService, 'query').mockReturnValue(of(new HttpResponse({ body: businessPlanCollection })));
      const expectedCollection: IBusinessPlan[] = [businessPlan, ...businessPlanCollection];
      jest.spyOn(businessPlanService, 'addBusinessPlanToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ financialForecast });
      comp.ngOnInit();

      expect(businessPlanService.query).toHaveBeenCalled();
      expect(businessPlanService.addBusinessPlanToCollectionIfMissing).toHaveBeenCalledWith(businessPlanCollection, businessPlan);
      expect(comp.businessPlansCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const financialForecast: IFinancialForecast = { id: 'CBA' };
      const businessPlan: IBusinessPlan = { id: '17a5f11a-817c-40db-88a9-449354086107' };
      financialForecast.businessPlan = businessPlan;

      activatedRoute.data = of({ financialForecast });
      comp.ngOnInit();

      expect(comp.businessPlansCollection).toContain(businessPlan);
      expect(comp.financialForecast).toEqual(financialForecast);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFinancialForecast>>();
      const financialForecast = { id: 'ABC' };
      jest.spyOn(financialForecastFormService, 'getFinancialForecast').mockReturnValue(financialForecast);
      jest.spyOn(financialForecastService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ financialForecast });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: financialForecast }));
      saveSubject.complete();

      // THEN
      expect(financialForecastFormService.getFinancialForecast).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(financialForecastService.update).toHaveBeenCalledWith(expect.objectContaining(financialForecast));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFinancialForecast>>();
      const financialForecast = { id: 'ABC' };
      jest.spyOn(financialForecastFormService, 'getFinancialForecast').mockReturnValue({ id: null });
      jest.spyOn(financialForecastService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ financialForecast: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: financialForecast }));
      saveSubject.complete();

      // THEN
      expect(financialForecastFormService.getFinancialForecast).toHaveBeenCalled();
      expect(financialForecastService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFinancialForecast>>();
      const financialForecast = { id: 'ABC' };
      jest.spyOn(financialForecastService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ financialForecast });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(financialForecastService.update).toHaveBeenCalled();
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
