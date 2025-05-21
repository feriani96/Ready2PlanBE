import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRevenueForecast } from '../revenue-forecast.model';
import { RevenueForecastService } from '../service/revenue-forecast.service';

@Injectable({ providedIn: 'root' })
export class RevenueForecastRoutingResolveService implements Resolve<IRevenueForecast | null> {
  constructor(protected service: RevenueForecastService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IRevenueForecast | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((revenueForecast: HttpResponse<IRevenueForecast>) => {
          if (revenueForecast.body) {
            return of(revenueForecast.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
