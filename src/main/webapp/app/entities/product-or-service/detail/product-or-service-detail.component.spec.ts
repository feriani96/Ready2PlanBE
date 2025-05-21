import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ProductOrServiceDetailComponent } from './product-or-service-detail.component';

describe('ProductOrService Management Detail Component', () => {
  let comp: ProductOrServiceDetailComponent;
  let fixture: ComponentFixture<ProductOrServiceDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProductOrServiceDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ productOrService: { id: 'ABC' } }) },
        },
      ],
    })
      .overrideTemplate(ProductOrServiceDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ProductOrServiceDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load productOrService on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.productOrService).toEqual(expect.objectContaining({ id: 'ABC' }));
    });
  });
});
