import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ProductOrServiceService } from '../service/product-or-service.service';

import { ProductOrServiceComponent } from './product-or-service.component';

describe('ProductOrService Management Component', () => {
  let comp: ProductOrServiceComponent;
  let fixture: ComponentFixture<ProductOrServiceComponent>;
  let service: ProductOrServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'product-or-service', component: ProductOrServiceComponent }]),
        HttpClientTestingModule,
      ],
      declarations: [ProductOrServiceComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(ProductOrServiceComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProductOrServiceComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ProductOrServiceService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 'ABC' }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.productOrServices?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });

  describe('trackId', () => {
    it('Should forward to productOrServiceService', () => {
      const entity = { id: 'ABC' };
      jest.spyOn(service, 'getProductOrServiceIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getProductOrServiceIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
