import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { BusinessPlanFormService } from './business-plan-form.service';
import { BusinessPlanService } from '../service/business-plan.service';
import { IBusinessPlan } from '../business-plan.model';

import { BusinessPlanUpdateComponent } from './business-plan-update.component';

describe('BusinessPlan Management Update Component', () => {
  let comp: BusinessPlanUpdateComponent;
  let fixture: ComponentFixture<BusinessPlanUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let businessPlanFormService: BusinessPlanFormService;
  let businessPlanService: BusinessPlanService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [BusinessPlanUpdateComponent],
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
      .overrideTemplate(BusinessPlanUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BusinessPlanUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    businessPlanFormService = TestBed.inject(BusinessPlanFormService);
    businessPlanService = TestBed.inject(BusinessPlanService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const businessPlan: IBusinessPlan = { id: 'CBA' };

      activatedRoute.data = of({ businessPlan });
      comp.ngOnInit();

      expect(comp.businessPlan).toEqual(businessPlan);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBusinessPlan>>();
      const businessPlan = { id: 'ABC' };
      jest.spyOn(businessPlanFormService, 'getBusinessPlan').mockReturnValue(businessPlan);
      jest.spyOn(businessPlanService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ businessPlan });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: businessPlan }));
      saveSubject.complete();

      // THEN
      expect(businessPlanFormService.getBusinessPlan).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(businessPlanService.update).toHaveBeenCalledWith(expect.objectContaining(businessPlan));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBusinessPlan>>();
      const businessPlan = { id: 'ABC' };
      jest.spyOn(businessPlanFormService, 'getBusinessPlan').mockReturnValue({ id: null });
      jest.spyOn(businessPlanService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ businessPlan: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: businessPlan }));
      saveSubject.complete();

      // THEN
      expect(businessPlanFormService.getBusinessPlan).toHaveBeenCalled();
      expect(businessPlanService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBusinessPlan>>();
      const businessPlan = { id: 'ABC' };
      jest.spyOn(businessPlanService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ businessPlan });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(businessPlanService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
