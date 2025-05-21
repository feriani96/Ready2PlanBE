import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { RevenueForecastFormService } from './revenue-forecast-form.service';
import { RevenueForecastService } from '../service/revenue-forecast.service';
import { IRevenueForecast } from '../revenue-forecast.model';
import { IProductOrService } from 'app/entities/product-or-service/product-or-service.model';
import { ProductOrServiceService } from 'app/entities/product-or-service/service/product-or-service.service';
import { IFinancialForecast } from 'app/entities/financial-forecast/financial-forecast.model';
import { FinancialForecastService } from 'app/entities/financial-forecast/service/financial-forecast.service';

import { RevenueForecastUpdateComponent } from './revenue-forecast-update.component';

describe('RevenueForecast Management Update Component', () => {
  let comp: RevenueForecastUpdateComponent;
  let fixture: ComponentFixture<RevenueForecastUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let revenueForecastFormService: RevenueForecastFormService;
  let revenueForecastService: RevenueForecastService;
  let productOrServiceService: ProductOrServiceService;
  let financialForecastService: FinancialForecastService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [RevenueForecastUpdateComponent],
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
      .overrideTemplate(RevenueForecastUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RevenueForecastUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    revenueForecastFormService = TestBed.inject(RevenueForecastFormService);
    revenueForecastService = TestBed.inject(RevenueForecastService);
    productOrServiceService = TestBed.inject(ProductOrServiceService);
    financialForecastService = TestBed.inject(FinancialForecastService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call ProductOrService query and add missing value', () => {
      const revenueForecast: IRevenueForecast = { id: 'CBA' };
      const product: IProductOrService = { id: '97c22c7b-faa3-45fc-b04d-ad01a371772b' };
      revenueForecast.product = product;

      const productOrServiceCollection: IProductOrService[] = [{ id: '27c9343a-23d7-4bcb-8e0e-9f5487fadb07' }];
      jest.spyOn(productOrServiceService, 'query').mockReturnValue(of(new HttpResponse({ body: productOrServiceCollection })));
      const additionalProductOrServices = [product];
      const expectedCollection: IProductOrService[] = [...additionalProductOrServices, ...productOrServiceCollection];
      jest.spyOn(productOrServiceService, 'addProductOrServiceToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ revenueForecast });
      comp.ngOnInit();

      expect(productOrServiceService.query).toHaveBeenCalled();
      expect(productOrServiceService.addProductOrServiceToCollectionIfMissing).toHaveBeenCalledWith(
        productOrServiceCollection,
        ...additionalProductOrServices.map(expect.objectContaining)
      );
      expect(comp.productOrServicesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call FinancialForecast query and add missing value', () => {
      const revenueForecast: IRevenueForecast = { id: 'CBA' };
      const forecast: IFinancialForecast = { id: '8c4a841f-5e71-498d-ad25-e0a2b1fff517' };
      revenueForecast.forecast = forecast;

      const financialForecastCollection: IFinancialForecast[] = [{ id: 'c163ccc3-3acf-4cf4-9e4a-8f6457d42e1e' }];
      jest.spyOn(financialForecastService, 'query').mockReturnValue(of(new HttpResponse({ body: financialForecastCollection })));
      const additionalFinancialForecasts = [forecast];
      const expectedCollection: IFinancialForecast[] = [...additionalFinancialForecasts, ...financialForecastCollection];
      jest.spyOn(financialForecastService, 'addFinancialForecastToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ revenueForecast });
      comp.ngOnInit();

      expect(financialForecastService.query).toHaveBeenCalled();
      expect(financialForecastService.addFinancialForecastToCollectionIfMissing).toHaveBeenCalledWith(
        financialForecastCollection,
        ...additionalFinancialForecasts.map(expect.objectContaining)
      );
      expect(comp.financialForecastsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const revenueForecast: IRevenueForecast = { id: 'CBA' };
      const product: IProductOrService = { id: '156a160c-29ac-4c50-a881-ae70c098ed5c' };
      revenueForecast.product = product;
      const forecast: IFinancialForecast = { id: '03d301b1-ab39-4654-b3bf-8109d5951853' };
      revenueForecast.forecast = forecast;

      activatedRoute.data = of({ revenueForecast });
      comp.ngOnInit();

      expect(comp.productOrServicesSharedCollection).toContain(product);
      expect(comp.financialForecastsSharedCollection).toContain(forecast);
      expect(comp.revenueForecast).toEqual(revenueForecast);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRevenueForecast>>();
      const revenueForecast = { id: 'ABC' };
      jest.spyOn(revenueForecastFormService, 'getRevenueForecast').mockReturnValue(revenueForecast);
      jest.spyOn(revenueForecastService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ revenueForecast });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: revenueForecast }));
      saveSubject.complete();

      // THEN
      expect(revenueForecastFormService.getRevenueForecast).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(revenueForecastService.update).toHaveBeenCalledWith(expect.objectContaining(revenueForecast));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRevenueForecast>>();
      const revenueForecast = { id: 'ABC' };
      jest.spyOn(revenueForecastFormService, 'getRevenueForecast').mockReturnValue({ id: null });
      jest.spyOn(revenueForecastService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ revenueForecast: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: revenueForecast }));
      saveSubject.complete();

      // THEN
      expect(revenueForecastFormService.getRevenueForecast).toHaveBeenCalled();
      expect(revenueForecastService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRevenueForecast>>();
      const revenueForecast = { id: 'ABC' };
      jest.spyOn(revenueForecastService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ revenueForecast });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(revenueForecastService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareProductOrService', () => {
      it('Should forward to productOrServiceService', () => {
        const entity = { id: 'ABC' };
        const entity2 = { id: 'CBA' };
        jest.spyOn(productOrServiceService, 'compareProductOrService');
        comp.compareProductOrService(entity, entity2);
        expect(productOrServiceService.compareProductOrService).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareFinancialForecast', () => {
      it('Should forward to financialForecastService', () => {
        const entity = { id: 'ABC' };
        const entity2 = { id: 'CBA' };
        jest.spyOn(financialForecastService, 'compareFinancialForecast');
        comp.compareFinancialForecast(entity, entity2);
        expect(financialForecastService.compareFinancialForecast).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
