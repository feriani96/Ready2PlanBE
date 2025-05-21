import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { BusinessPlanDetailComponent } from './business-plan-detail.component';

describe('BusinessPlan Management Detail Component', () => {
  let comp: BusinessPlanDetailComponent;
  let fixture: ComponentFixture<BusinessPlanDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BusinessPlanDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ businessPlan: { id: 'ABC' } }) },
        },
      ],
    })
      .overrideTemplate(BusinessPlanDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(BusinessPlanDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load businessPlan on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.businessPlan).toEqual(expect.objectContaining({ id: 'ABC' }));
    });
  });
});
