import { TestBed } from '@angular/core/testing';

import { WebsocketServicre } from './websocket-servicre';

describe('WebsocketServicre', () => {
  let service: WebsocketServicre;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WebsocketServicre);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
