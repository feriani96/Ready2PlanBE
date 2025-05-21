import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IBusinessPlan, NewBusinessPlan } from '../business-plan.model';

export type PartialUpdateBusinessPlan = Partial<IBusinessPlan> & Pick<IBusinessPlan, 'id'>;

export type EntityResponseType = HttpResponse<IBusinessPlan>;
export type EntityArrayResponseType = HttpResponse<IBusinessPlan[]>;

@Injectable({ providedIn: 'root' })
export class BusinessPlanService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/business-plans');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/business-plans');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(businessPlan: NewBusinessPlan): Observable<EntityResponseType> {
    return this.http.post<IBusinessPlan>(this.resourceUrl, businessPlan, { observe: 'response' });
  }

  update(businessPlan: IBusinessPlan): Observable<EntityResponseType> {
    return this.http.put<IBusinessPlan>(`${this.resourceUrl}/${this.getBusinessPlanIdentifier(businessPlan)}`, businessPlan, {
      observe: 'response',
    });
  }

  partialUpdate(businessPlan: PartialUpdateBusinessPlan): Observable<EntityResponseType> {
    return this.http.patch<IBusinessPlan>(`${this.resourceUrl}/${this.getBusinessPlanIdentifier(businessPlan)}`, businessPlan, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IBusinessPlan>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IBusinessPlan[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IBusinessPlan[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getBusinessPlanIdentifier(businessPlan: Pick<IBusinessPlan, 'id'>): string {
    return businessPlan.id;
  }

  compareBusinessPlan(o1: Pick<IBusinessPlan, 'id'> | null, o2: Pick<IBusinessPlan, 'id'> | null): boolean {
    return o1 && o2 ? this.getBusinessPlanIdentifier(o1) === this.getBusinessPlanIdentifier(o2) : o1 === o2;
  }

  addBusinessPlanToCollectionIfMissing<Type extends Pick<IBusinessPlan, 'id'>>(
    businessPlanCollection: Type[],
    ...businessPlansToCheck: (Type | null | undefined)[]
  ): Type[] {
    const businessPlans: Type[] = businessPlansToCheck.filter(isPresent);
    if (businessPlans.length > 0) {
      const businessPlanCollectionIdentifiers = businessPlanCollection.map(
        businessPlanItem => this.getBusinessPlanIdentifier(businessPlanItem)!
      );
      const businessPlansToAdd = businessPlans.filter(businessPlanItem => {
        const businessPlanIdentifier = this.getBusinessPlanIdentifier(businessPlanItem);
        if (businessPlanCollectionIdentifiers.includes(businessPlanIdentifier)) {
          return false;
        }
        businessPlanCollectionIdentifiers.push(businessPlanIdentifier);
        return true;
      });
      return [...businessPlansToAdd, ...businessPlanCollection];
    }
    return businessPlanCollection;
  }
}
