insert into service_type (code, description) values ('FIX', 'Blechreparatur') ON CONFLICT DO NOTHING;
insert into service_type (code, description) values ('WHE', 'Radwechsel');
insert into service_type (code, description) values ('MOT', 'Motorinstandsetzung');
insert into service_type (code, description) values ('OIL', 'Ölwechsel');
insert into service_type (code, description) values ('INS', 'Hauptuntersuchung');

insert into workshop (id, name, capacity, office_hours_start, office_hours_end, version)
    values (1, 'Autohaus-Schmidt', 2, '07:30', '18:00', 0);
insert into workshop (id, name, capacity, office_hours_start, office_hours_end, version)
    values (2, 'Meisterbetrieb-Bachstraße', 3, '08:00', '19:30', 0);

insert into offered_service (workshop_id, service_type_code, duration) values (1, 'MOT', 240);
insert into offered_service (workshop_id, service_type_code, duration) values (1, 'OIL', 15);
insert into offered_service (workshop_id, service_type_code, duration) values (1, 'WHE', 30);

insert into offered_service (workshop_id, service_type_code, duration) values (2, 'FIX', 180);
insert into offered_service (workshop_id, service_type_code, duration) values (2, 'OIL', 10);
insert into offered_service (workshop_id, service_type_code, duration) values (2, 'INS', 60);
