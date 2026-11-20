-- Conteúdo do portfólio: uma linha por idioma ('pt' ou 'en').
create table profile (
    lang        varchar(2) primary key,
    name        text   not null,
    role        text   not null,
    location    text   not null,
    status      text   not null,
    bio         text   not null,
    tags        text[] not null default '{}'
);

create table experience (
    id          bigserial primary key,
    lang        varchar(2) not null,
    position    int    not null,
    company     text   not null,
    role        text   not null,
    period      text   not null,
    location    text   not null,
    description text   not null,
    highlights  text[] not null default '{}',
    unique (lang, position)
);

create table project (
    id          bigserial primary key,
    lang        varchar(2) not null,
    position    int    not null,
    slug        text   not null,
    name        text   not null,
    description text   not null,
    highlights  text[] not null default '{}',
    tags        text[] not null default '{}',
    status      text   not null,
    repo_url    text,
    demo_url    text,
    unique (lang, position),
    unique (lang, slug)
);

create table skill_group (
    id          bigserial primary key,
    lang        varchar(2) not null,
    position    int    not null,
    title       text   not null,
    skills      text[] not null default '{}',
    unique (lang, position)
);

create table education (
    id          bigserial primary key,
    lang        varchar(2) not null,
    position    int  not null,
    course      text not null,
    institution text not null,
    period      text not null,
    unique (lang, position)
);

-- Formulário de contato.
create table contact_message (
    id          uuid primary key,
    name        varchar(80)   not null,
    email       varchar(254)  not null,
    message     varchar(2000) not null,
    status      varchar(20)   not null,
    received_at timestamptz   not null,
    updated_at  timestamptz   not null default now()
);

create table contact_trace (
    id          bigserial primary key,
    contact_id  uuid        not null references contact_message (id),
    step        varchar(20) not null,
    at          timestamptz not null,
    took_ms     bigint      not null,
    component   varchar(60) not null
);

create index contact_trace_by_contact on contact_trace (contact_id, at, id);
