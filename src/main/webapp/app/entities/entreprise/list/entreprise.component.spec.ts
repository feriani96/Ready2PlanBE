import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { EntrepriseService } from '../service/entreprise.service';

import { EntrepriseComponent } from './entreprise.component';

describe('Entreprise Management Component', () => {
  let comp: EntrepriseComponent;
  let fixture: ComponentFixture<EntrepriseComponent>;
  let service: EntrepriseService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'entreprise', component: EntrepriseComponent }]), HttpClientTestingModule],
      declarations: [EntrepriseComponent],
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
      .overrideTemplate(EntrepriseComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EntrepriseComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(EntrepriseService);

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
    expect(comp.entreprises?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });

  describe('trackId', () => {
    it('Should forward to entrepriseService', () => {
      const entity = { id: 'ABC' };
      jest.spyOn(service, 'getEntrepriseIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getEntrepriseIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
