import { Module } from '@nestjs/common';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { TypeOrmModule } from '@nestjs/typeorm';
import { createObserveModule } from '@nestjs/observe';

import { AppController } from './app.controller.js';
import { AppService } from './app.service.js';

import { BooksModule } from './books/books.module.js';

import { databaseProviders } from './database.provider.js';
import { RentalController } from './rentals/rental.controller.js';
import { RentalService } from './rentals/rental.service.js';
import { RentalRepository } from './rentals/rental.repository.js';

export const { ObserveModule, ObserveInstrument } = createObserveModule();

@Module({
  imports: [
    ConfigModule.forRoot({
      isGlobal: true,
    }),

    TypeOrmModule.forRootAsync({
      inject: [ConfigService],
      useFactory: (configService: ConfigService) => ({
        type: 'mysql',
        host: configService.getOrThrow<string>('DB_HOST'),
        port: 3306,
        username: configService.getOrThrow<string>('DB_USER'),
        password: configService.getOrThrow<string>('DB_PASSWORD'),
        database: configService.getOrThrow<string>('DB_NAME'),
        autoLoadEntities: true,
        synchronize: false,
      }),
    }),

    BooksModule,
  ],

  controllers: [AppController, RentalController],

  providers: [
    ...databaseProviders,
    AppService,
    RentalService,
    RentalRepository,
  ],

  exports: [...databaseProviders],
})
export class AppModule {}
