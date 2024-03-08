import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {BackupService} from "../../services/api/backup.service";
import {Backup} from "../../common/types/backup";
import {GuiUtilsService} from "../../services/gui-utils.service";
import {ScreenMessageService} from "../../services/screen-message.service";

@Component({
  selector: 'app-backup-manager',
  templateUrl: './backup-manager.component.html',
  styleUrls: ['./backup-manager.component.scss'],
})
export class BackupManagerComponent implements OnInit {

  @Input({required: true}) resourceType!: 'TEMPLATE' | 'DATASOURCE' | 'RENDERER';
  @Input({required: true}) resourceId!: string;
  @Input({required: true}) canWrite!: boolean;
  @Output() restoreEmitter = new EventEmitter<any>();

  backups: Backup[] = [];

  constructor(private backupService: BackupService,
              private screenMessageService: ScreenMessageService) {
  }

  ngOnInit() {
    this.backupService.getBackups(this.resourceType, this.resourceId).then(b => this.backups = b);
  }

  doBackup() {
    return this.screenMessageService.loadingWrapper(async () => {
      try {
        this.backups.unshift(await this.backupService.doBackup(this.resourceType, this.resourceId));
        await this.screenMessageService.showDone();
      } catch (e) {
        await this.screenMessageService.showError(e);
      }
    })
  }

  delete(b: Backup) {
    return this.screenMessageService.showDeleteAlert(() => {
      return this.screenMessageService.loadingWrapper(async () => {
        await this.backupService.delete(b.identifier);
        this.backups = this.backups.filter(bb => bb.identifier !== b.identifier);
      })
    });
  }

  restore(b: Backup) {
    return this.screenMessageService.loadingWrapper(async () => {
      const backup = await this.backupService.findById(b.identifier);
      this.restoreEmitter.emit(JSON.parse(backup.data));
    });
  }
}
