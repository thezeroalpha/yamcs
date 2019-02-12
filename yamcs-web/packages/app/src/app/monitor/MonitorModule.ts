import { NgModule } from '@angular/core';
import { MdbModule } from '../mdb/MdbModule';
import { SharedModule } from '../shared/SharedModule';
import { AcknowledgeAlarmDialog } from './alarms/AcknowledgeAlarmDialog';
import { AlarmDetail } from './alarms/AlarmDetail';
import { DownloadDumpDialog } from './archive/DownloadDumpDialog';
import { JumpToDialog } from './archive/JumpToDialog';
import { TimelineTooltip } from './archive/TimelineTooltip';
import { CreateDisplayDialog } from './displays/CreateDisplayDialog';
import { DisplayFilePageDirtyDialog } from './displays/DisplayFilePageDirtyDialog';
import { ExportArchiveDataDialog } from './displays/ExportArchiveDataDialog';
import { ImageViewer } from './displays/ImageViewer';
import { MultipleParameterTable } from './displays/MultipleParameterTable';
import { OpiDisplayViewer } from './displays/OpiDisplayViewer';
import { ParameterTableViewer } from './displays/ParameterTableViewer';
import { ParameterTableViewerControls } from './displays/ParameterTableViewerControls';
import { RenameDisplayDialog } from './displays/RenameDisplayDialog';
import { ScriptViewer } from './displays/ScriptViewer';
import { ScrollingParameterTable } from './displays/ScrollingParameterTable';
import { TextViewer } from './displays/TextViewer';
import { UploadFilesDialog } from './displays/UploadFilesDialog';
import { UssDisplayViewer } from './displays/UssDisplayViewer';
import { UssDisplayViewerControls } from './displays/UssDisplayViewerControls';
import { ViewerControlsHost } from './displays/ViewerControlsHost';
import { ViewerHost } from './displays/ViewerHost';
import { CreateEventDialog } from './events/CreateEventDialog';
import { EventSeverity } from './events/EventSeverity';
import { PageContentHost } from './ext/PageContentHost';
import { CreateLayoutDialog } from './layouts/CreateLayoutDialog';
import { DisplayNavigator } from './layouts/DisplayNavigator';
import { Frame } from './layouts/Frame';
import { FrameHost } from './layouts/FrameHost';
import { Layout } from './layouts/Layout';
import { RenameLayoutDialog } from './layouts/RenameLayoutDialog';
import { MonitorRoutingModule, routingComponents } from './MonitorRoutingModule';
import { DisplayTypePipe } from './pipes/DisplayTypePipe';
import { CommandQueuesTable } from './processors/CommandQueuesTable';
import { QueuedCommandsTable } from './processors/QueuedCommandsTable';
import { TmStatsTable } from './processors/TmStatsTable';
import { RecordComponent } from './table/RecordComponent';
import { ShowEnumDialog } from './table/ShowEnumDialog';

const dialogComponents = [
  AcknowledgeAlarmDialog,
  CreateDisplayDialog,
  CreateEventDialog,
  CreateLayoutDialog,
  DisplayFilePageDirtyDialog,
  DownloadDumpDialog,
  ExportArchiveDataDialog,
  JumpToDialog,
  RenameDisplayDialog,
  RenameLayoutDialog,
  ShowEnumDialog,
  UploadFilesDialog,
];

const pipes = [
  DisplayTypePipe,
];

const directives = [
  FrameHost,
  PageContentHost,
  ViewerControlsHost,
  ViewerHost,
];

const viewers = [
  ImageViewer,
  OpiDisplayViewer,
  ParameterTableViewer,
  ParameterTableViewerControls,
  ScriptViewer,
  TextViewer,
  UssDisplayViewer,
  UssDisplayViewerControls,
];

@NgModule({
  imports: [
    SharedModule,
    MdbModule,
    MonitorRoutingModule,
  ],
  declarations: [
    routingComponents,
    dialogComponents,
    directives,
    pipes,
    viewers,
    AlarmDetail,
    CommandQueuesTable,
    DisplayNavigator,
    Frame,
    EventSeverity,
    Layout,
    MultipleParameterTable,
    QueuedCommandsTable,
    RecordComponent,
    ScrollingParameterTable,
    TimelineTooltip,
    TmStatsTable,
  ],
  entryComponents: [
    dialogComponents,
    viewers,
    Frame,
    TimelineTooltip,
  ]
})
export class MonitorModule {
}
