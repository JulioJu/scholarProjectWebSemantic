/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { ScholarProjectWebSemanticTestModule } from '../../../test.module';
import { AlbumSempicDeleteDialogComponent } from 'app/entities/album-sempic/album-sempic-delete-dialog.component';
import { AlbumSempicService } from 'app/entities/album-sempic/album-sempic.service';

describe('Component Tests', () => {
  describe('AlbumSempic Management Delete Component', () => {
    let comp: AlbumSempicDeleteDialogComponent;
    let fixture: ComponentFixture<AlbumSempicDeleteDialogComponent>;
    let service: AlbumSempicService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [ScholarProjectWebSemanticTestModule],
        declarations: [AlbumSempicDeleteDialogComponent]
      })
        .overrideTemplate(AlbumSempicDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(AlbumSempicDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(AlbumSempicService);
      mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
      mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
    });

    describe('confirmDelete', () => {
      it('Should call delete service on confirmDelete', inject(
        [],
        fakeAsync(() => {
          // GIVEN
          spyOn(service, 'delete').and.returnValue(of({}));

          // WHEN
          comp.confirmDelete(123);
          tick();

          // THEN
          expect(service.delete).toHaveBeenCalledWith(123);
          expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
          expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
        })
      ));
    });
  });
});
