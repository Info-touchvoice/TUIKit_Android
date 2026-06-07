<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('events', function (Blueprint $table) {
            $table->id();
            $table->enum('event_type', [
                'seasonal', 'vip', 'recharge', 'consumption', 'ranking',
                'family', 'election', 'login', 'cooperative', 'custom'
            ]);
            $table->string('name', 255);
            $table->longText('description')->nullable();
            $table->string('banner_image_url', 500)->nullable();
            $table->string('icon_url', 500)->nullable();
            $table->longText('event_rules')->nullable();
            $table->dateTime('start_date');
            $table->dateTime('end_date');
            $table->boolean('is_active')->default(true);
            $table->boolean('is_published')->default(false);
            $table->unsignedBigInteger('created_by')->nullable();
            $table->timestamps();
            $table->softDeletes();

            $table->index('event_type');
            $table->index('is_active');
            $table->index('start_date');
            $table->index('end_date');
            $table->foreign('created_by')->references('id')->on('admin_users')->onDelete('set null');
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('events');
    }
};
